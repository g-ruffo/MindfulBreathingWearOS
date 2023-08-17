package ca.veltus.mindfulbreathingwearos.domain.repository

import androidx.health.services.client.data.DeltaDataType
import ca.veltus.mindfulbreathingwearos.common.HeartRateResponse
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate
import kotlinx.coroutines.flow.Flow

interface BreathingRepository {

    suspend fun getCapabilities(): Set<DeltaDataType<*, *>>

    fun heartRateMeasureFlow(): Flow<HeartRateResponse>

    fun getCacheItemCount(): Flow<Resource<Int>>

    fun getDatabaseItemCount(): Flow<Resource<Int>>

    fun getHeartRate(): Flow<Resource<HeartRate>>

    fun clearJob()

}