package ca.veltus.mindfulbreathingwearos.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.concurrent.futures.await
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import ca.veltus.mindfulbreathingwearos.common.HeartRateResponse
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.DatabaseStatsDTO
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.HeartRateDTO
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.toHeartRate
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.toHeartRateDTOList
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.toHeartRateCacheEntity
import ca.veltus.mindfulbreathingwearos.data.local.HeartRateDAO
import ca.veltus.mindfulbreathingwearos.data.local.entity.toHeartRateEntity
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseUpdateEvent
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import javax.inject.Inject

class BreathingRepositoryImpl @Inject constructor(
    private val heartRateDAO: HeartRateDAO,
    healthServicesClient: HealthServicesClient
) : BreathingRepository {
    //measureClient instance.
    private val measureClient = healthServicesClient.measureClient

    // In-Memory accumulation
    private val _uncachedHeartRates = MutableStateFlow<List<HeartRateDTO>>(emptyList())
    val uncachedHeartRates: StateFlow<List<HeartRateDTO>> = _uncachedHeartRates.asStateFlow()

    private val _databaseUpdates = MutableSharedFlow<DatabaseUpdateEvent>()
    private val databaseUpdates: SharedFlow<DatabaseUpdateEvent> = _databaseUpdates.asSharedFlow()

    private val isDatabaseConnected = MutableStateFlow(true)  // default is true

    private val job = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + job)

    init {
        repositoryScope.launch {
            saveToCacheAndDatabase()
        }
    }

    private suspend fun saveToCacheAndDatabase() {
        var counter = 0
        var dataToCache: List<HeartRateDTO> = emptyList()

        while (repositoryScope.isActive) {
            delay(3000) // Wait for 3 seconds
            counter += 3

            if (uncachedHeartRates.value.isNotEmpty()) {
                // Collect data and clear in-memory list inside synchronized block
                synchronized(_uncachedHeartRates) {
                    dataToCache = uncachedHeartRates.value.toList()
                    _uncachedHeartRates.value = emptyList()
                }

                var databaseUpdate = DatabaseUpdateEvent()
                try {
                    heartRateDAO.insertAllHeartRateCache(dataToCache.map { it.toHeartRateCacheEntity() })
                    databaseUpdate.cacheUpdated = true
                } catch (e: Exception) {
                    Log.e(TAG, "saveToCacheAndDatabase: ${e.message}")
                }

                // Every 60 seconds, move data from DB cache to permanent storage
                if (counter >= 6 && isDatabaseConnected.value) {
                    try {
                        // Assuming you have a separate mechanism for permanent storage.
                        val listFromCache = heartRateDAO.getAllFromCache()
                        // storeToPermanentStorage(lastMinuteData)
                        heartRateDAO.insertAllHeartRates(listFromCache.map { it.toHeartRateEntity() })
                        heartRateDAO.clearCache()
                        databaseUpdate.databaseUpdated = true
                        counter = 0
                    } catch (e: Exception) {
                        Log.e(TAG, "saveToCacheAndDatabase: ${e.message}")
                    }
                }
                _databaseUpdates.emit(databaseUpdate)
            }
        }
    }

    override fun clearJob() {
        job.cancel() // This cancels all coroutines launched in this scope
    }

    override fun getDatabaseConnectionState(): Flow<Boolean> {
        return isDatabaseConnected.asStateFlow()
    }

    override fun toggleDatabaseConnection() {
        isDatabaseConnected.value = !isDatabaseConnected.value
    }

    override suspend fun getCapabilities(): Set<DeltaDataType<*, *>> {
        val capabilities = measureClient.getCapabilitiesAsync().await()
        return capabilities.supportedDataTypesMeasure
    }


    override fun heartRateMeasureFlow() = callbackFlow { //cold flow
        //here the call back code.
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                // Only send back DataTypeAvailability (not LocationAvailability)
                if (availability is DataTypeAvailability) {
                    trySendBlocking(HeartRateResponse.Availability(availability))
                }
            }

            override fun onDataReceived(data: DataPointContainer) {
                val dataList = data.getData(DataType.HEART_RATE_BPM)
                val heartRate = dataList.toHeartRateDTOList()
                Log.d(TAG, "dataList size -- ${dataList.size} -- Heart Rate BPM is -- ${heartRate.last().value} -- Accuracy is -- ${heartRate.last().accuracy} -- Time is -- ${heartRate.last().timeInstant}")
                val response = HeartRateResponse.Data(heartRate = heartRate.last().toHeartRate())
                trySendBlocking(response)

                synchronized(_uncachedHeartRates) {
                    val currentList = _uncachedHeartRates.value.toMutableList()
                    currentList.add(heartRate.last())
                    _uncachedHeartRates.value = currentList
                }
            }

            override fun onRegistrationFailed(throwable: Throwable) {
                super.onRegistrationFailed(throwable)
                val error = throwable.message ?: "MeasureCallback onRegistrationFailed"
                Log.e(TAG, "onRegistrationFailed: $error")
                trySendBlocking(HeartRateResponse.Error(error))
            }
        }

        //register the measureclient callback
        measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)

        awaitClose {
            //Unregistering measureclient callback
            runBlocking {
                measureClient.unregisterMeasureCallbackAsync(DataType.HEART_RATE_BPM, callback)
                    .await()
            }
        }
    }

    override fun getDatabaseUpdates(): Flow<DatabaseUpdateEvent> {
        return databaseUpdates
    }

    override fun getCacheStats(): Flow<Resource<DatabaseStatsDTO>> = flow {
        emit(Resource.Loading())
        try {
            val data = heartRateDAO.getCacheStats()
            emit(Resource.Success(data = data))
        } catch (e: Exception) {
            Log.e(TAG, "getCacheItemCount: ${e.message}")
            emit(Resource.Error(message = "${e.message}"))
        }
    }

    override fun getDatabaseStats(): Flow<Resource<DatabaseStatsDTO>> = flow {
        emit(Resource.Loading())
        try {
            val data = heartRateDAO.getDatabaseStats()

            emit(Resource.Success(data = data))
        } catch (e: Exception) {
            Log.e(TAG, "getDatabaseItemCount: ${e.message}")
            emit(Resource.Error(message = "${e.message}"))
        }
    }

    override fun getUncachedStats(): Flow<Resource<DatabaseStatsDTO>> = flow {
        emit(Resource.Loading())
        try {
            val count = uncachedHeartRates.value.size
            val lastAdded = uncachedHeartRates.value.maxOfOrNull { it.timeInstant } ?: 0L
            val data = DatabaseStatsDTO(count, lastAdded)

            emit(Resource.Success(data = data))
        } catch (e: Exception) {
            Log.e(TAG, "getAccumulatedDataStats: ${e.message}")
            emit(Resource.Error(message = "${e.message}"))
        }
    }
}

