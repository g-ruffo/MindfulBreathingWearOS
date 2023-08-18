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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.Exception
import javax.inject.Inject

class BreathingRepositoryImpl @Inject constructor(
    private val heartRateDAO: HeartRateDAO, healthServicesClient: HealthServicesClient
) : BreathingRepository {
    private val uncachedHeartRatesMutex = Mutex()

    // MeasureClient instance
    private val measureClient = healthServicesClient.measureClient

    // Uncached heart rate data stored in memory for three seconds
    private val _uncachedHeartRates = MutableStateFlow<List<HeartRateDTO>>(emptyList())
    private val uncachedHeartRates: StateFlow<List<HeartRateDTO>> =
        _uncachedHeartRates.asStateFlow()

    // Stores a record of which data sources have been updated
    private val _databaseUpdates = MutableSharedFlow<DatabaseUpdateEvent>()
    private val databaseUpdates: SharedFlow<DatabaseUpdateEvent> = _databaseUpdates.asSharedFlow()

    private val _isDatabaseConnected = MutableStateFlow(true)
    private val isDatabaseConnected: StateFlow<Boolean> = _isDatabaseConnected.asStateFlow()

    // Remaining time left on count down timer
    private val _timerTimeMillis = MutableStateFlow(FOUR_MINUTES_IN_MILLISECONDS)
    private val timerTimeMillis: StateFlow<Long> = _timerTimeMillis.asStateFlow()

    private val job = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + job)

    init {
        repositoryScope.launch {
            saveToCacheAndDatabase()
        }
        repositoryScope.launch {
            timerCountdown()
        }
    }

    private suspend fun timerCountdown() {
        while (repositoryScope.isActive) {
            // Start the countdown timer when database is disconnected
            while (!isDatabaseConnected.value) {
                // Continue counting down until timer reaches 0
                if (timerTimeMillis.value > 0) {
                    // Emit the remaining time of the countdown timer to the view model minus 1 second
                    _timerTimeMillis.emit(_timerTimeMillis.value - ONE_SECOND_IN_MILLISECONDS)
                    delay(ONE_SECOND_IN_MILLISECONDS)
                } else {
                    // Once the countdown timer reaches 0 reconnect the database
                    _isDatabaseConnected.emit(true)
                }
            }
        }
    }

    // Save the collected data to the cache every 3 seconds and save cache to database every minute
    private suspend fun saveToCacheAndDatabase() {
        var secondsCounter = 0
        var dataToCache: List<HeartRateDTO>
        while (repositoryScope.isActive) {
            // Wait for 3 seconds
            delay(THREE_SECOND_IN_MILLISECONDS)
            secondsCounter += THREE_SECONDS
            // Check for uncached data before proceeding
            if (uncachedHeartRates.value.isNotEmpty()) {
                // Collect data and clear in-memory list inside synchronized block
                uncachedHeartRatesMutex.withLock {
                    dataToCache = uncachedHeartRates.value.toList()
                    _uncachedHeartRates.value = emptyList()
                }
                try {
                    // Save uncached data to cache
                    heartRateDAO.insertAllHeartRateCache(dataToCache.map { it.toHeartRateCacheEntity() })
                    // Notify view model that the uncached and cached data has been updated
                    _databaseUpdates.emit(
                        DatabaseUpdateEvent(
                            cacheUpdated = true, uncachedUpdated = true
                        )
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "saveToCacheAndDatabase: ${e.message}")
                }

                // Every 60 seconds, move data from database cache to permanent storage
                if (secondsCounter >= SIXTY_SECONDS && isDatabaseConnected.value) {
                    try {
                        val listFromCache = heartRateDAO.getAllFromCache()
                        // Insert all cached data into database
                        heartRateDAO.insertAllHeartRates(listFromCache.map { it.toHeartRateEntity() })
                        // Clear the cache after data has been saved
                        heartRateDAO.clearCache()
                        // Notify view model that the database has been updated
                        _databaseUpdates.emit(DatabaseUpdateEvent(databaseUpdated = true))
                        // Reset the counter
                        secondsCounter = 0
                    } catch (e: Exception) {
                        Log.e(TAG, "saveToCacheAndDatabase: ${e.message}")
                    }
                }
            }
        }
    }

    // Cancels all coroutines launched in this scope
    override fun clearJob() {
        job.cancel()
    }

    override fun getDatabaseConnectionState(): Flow<Boolean> {
        return isDatabaseConnected
    }

    // Toggle the databases connection status and set the timers countdown time
    override fun toggleDatabaseConnection(timerTime: Long) {
        _timerTimeMillis.value = timerTime
        _isDatabaseConnected.value = !_isDatabaseConnected.value
    }

    override fun getTimerTimeMillis(): Flow<Long> {
        return timerTimeMillis
    }

    // Returns all capabilities of the device
    override suspend fun getCapabilities(): Set<DeltaDataType<*, *>> {
        val capabilities = measureClient.getCapabilitiesAsync().await()
        return capabilities.supportedDataTypesMeasure
    }

    // Create cold flow to fetch heart rate data
    override fun heartRateMeasureFlow() = callbackFlow {
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>, availability: Availability
            ) {
                // Send back only DataTypeAvailability and not LocationAvailability
                if (availability is DataTypeAvailability) {
                    trySendBlocking(HeartRateResponse.Availability(availability))
                }
            }

            override fun onDataReceived(data: DataPointContainer) {
                val dataList = data.getData(DataType.HEART_RATE_BPM)
                val heartRate = dataList.toHeartRateDTOList()
                // Get the last heart rate data from the list for immediate display
                val response = HeartRateResponse.Data(heartRate = heartRate.last().toHeartRate())
                trySendBlocking(response)

                repositoryScope.launch {
                    uncachedHeartRatesMutex.withLock {
                        // Store the retrieved data in temporary memory
                        val currentList = _uncachedHeartRates.value.toMutableList()
                        currentList.add(heartRate.last())
                        _uncachedHeartRates.value = currentList
                    }
                    // Notify view model that the temporary data has been updated
                    _databaseUpdates.emit(DatabaseUpdateEvent(uncachedUpdated = true))
                }
            }

            override fun onRegistrationFailed(throwable: Throwable) {
                super.onRegistrationFailed(throwable)
                val error = throwable.message ?: "MeasureCallback onRegistrationFailed"
                Log.e(TAG, "onRegistrationFailed: $error")
                trySendBlocking(HeartRateResponse.Error(error))
            }
        }

        // Register the MeasureClient callback
        measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)

        awaitClose {
            //Unregister the MeasureClient callback
            runBlocking {
                measureClient.unregisterMeasureCallbackAsync(DataType.HEART_RATE_BPM, callback)
                    .await()
            }
        }
    }

    override fun getDatabaseUpdates(): Flow<DatabaseUpdateEvent> {
        return databaseUpdates
    }

    // Returns the current details of the stored cache
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

    // Returns the current saved details of the database
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

    // Returns the current details of the temporary stored data
    override fun getUncachedStats(): Flow<Resource<DatabaseStatsDTO>> = flow {
        emit(Resource.Loading())
        try {
            // Create a DatabaseStatsDTO object to send back to the view model
            val count = uncachedHeartRates.value.size
            val lastAdded = uncachedHeartRates.value.maxOfOrNull { it.timeInstant }
            val data = DatabaseStatsDTO(count, lastAdded)

            emit(Resource.Success(data = data))
        } catch (e: Exception) {
            Log.e(TAG, "getAccumulatedDataStats: ${e.message}")
            emit(Resource.Error(message = "${e.message}"))
        }
    }

    companion object {
        private const val ONE_SECOND_IN_MILLISECONDS: Long = 1000
        private const val THREE_SECOND_IN_MILLISECONDS: Long = 1000
        private const val FOUR_MINUTES_IN_MILLISECONDS: Long = 240000
        private const val SIXTY_SECONDS = 60
        private const val THREE_SECONDS = 3

    }
}

