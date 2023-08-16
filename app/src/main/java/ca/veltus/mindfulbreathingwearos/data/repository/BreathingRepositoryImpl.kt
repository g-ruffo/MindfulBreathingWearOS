package ca.veltus.mindfulbreathingwearos.data.repository

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.concurrent.futures.await
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.SampleDataPoint
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class BreathingRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
): BreathingRepository {
    //health service instance
    private val healthServicesClient = HealthServices.getClient(context)
    //measureClient instance.
    private val measureClient = healthServicesClient.measureClient

    suspend fun hasHeartRateCapability(): Boolean {
        val capabilities = measureClient.getCapabilitiesAsync().await()
        return (DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure)
    }

    fun heartRateMeasureFlow() = callbackFlow { //cold flow

        //here the call back code.
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                // Only send back DataTypeAvailability (not LocationAvailability)
                if (availability is DataTypeAvailability) {
                    //if availability == DataTypeAvailability.AVAILABLE true, then data is available and will recive in onDataRecieved Method.
                    trySendBlocking(MeasureMessage.MeasureAvailability(availability))
                }
            }

            override fun onDataReceived(data: DataPointContainer) {
                val heartRateBpm = data.getData(DataType.HEART_RATE_BPM)
                Log.d(TAG, "onDataReceived: Current Heart Rate BPM is -- ${heartRateBpm.last().value}")
                trySendBlocking(MeasureMessage.MeasureData(heartRateBpm))
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
}

sealed class MeasureMessage {
    class MeasureAvailability(val availability: DataTypeAvailability) : MeasureMessage()
    class MeasureData(val data: List<SampleDataPoint<Double>>) : MeasureMessage()
}