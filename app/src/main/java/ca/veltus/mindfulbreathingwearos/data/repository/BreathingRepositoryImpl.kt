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
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.HeartRateDTO
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.toDTOList
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.toHeartRate
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.toHeartRateCacheEntity
import ca.veltus.mindfulbreathingwearos.data.local.HeartRateDAO
import ca.veltus.mindfulbreathingwearos.data.local.entity.toHeartRateEntity
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class BreathingRepositoryImpl @Inject constructor(
    private val heartRateDAO: HeartRateDAO,
    healthServicesClient: HealthServicesClient
): BreathingRepository {
    //measureClient instance.
    private val measureClient = healthServicesClient.measureClient

    // In-Memory accumulation
    private val accumulatedData = mutableListOf<HeartRateDTO>()

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

        while (isActive) {
            delay(3000) // Wait for 3 seconds
            counter += 3

            // Collect data and clear in-memory list inside synchronized block
            synchronized(accumulatedData) {
                dataToCache = accumulatedData.toList()
                accumulatedData.clear()
            }

            heartRateDAO.insertAllHeartRateCache(dataToCache.map { it.toHeartRateCacheEntity() })

            // Every 60 seconds, move data from DB cache to permanent storage
            if (counter >= 60) {
                // Assuming you have a separate mechanism for permanent storage.
                val listFromCache = heartRateDAO.getAllFromCache()
                // storeToPermanentStorage(lastMinuteData)
                heartRateDAO.insertAllHeartRates(listFromCache.map { it.toHeartRateEntity() })
                heartRateDAO.clearCache()
                counter = 0
            }
        }
    }

    override fun clearJob() {
        job.cancel() // This cancels all coroutines launched in this scope
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
                    Log.d(TAG, "availability is DataTypeAvailability -- ${availability}")
                    trySendBlocking(HeartRateResponse.Availability(availability))
                }
            }

            override fun onDataReceived(data: DataPointContainer) {
                val dataList = data.getData(DataType.HEART_RATE_BPM)
                val heartRate = dataList.toDTOList()
                Log.d(TAG, "dataList size -- ${dataList.size} -- Heart Rate BPM is -- ${heartRate.last().value} -- Accuracy is -- ${heartRate.last().accuracy} -- Time is -- ${heartRate.last().timeInstant}")
                val response = HeartRateResponse.Data(heartRate = heartRate.last().toHeartRate())
                trySendBlocking(response)

                synchronized(accumulatedData) {
                    accumulatedData.add(heartRate.last()) // Store in the in-memory list
                }
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

    override fun getCacheItemCount(): Flow<Resource<Int>> {
        TODO("Not yet implemented")
    }

    override fun getDatabaseItemCount(): Flow<Resource<Int>> {
        TODO("Not yet implemented")
    }

    override fun getHeartRate(): Flow<Resource<HeartRate>> {
        TODO("Not yet implemented")
    }
}

