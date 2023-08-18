package ca.veltus.mindfulbreathingwearos.domain.repository

import androidx.health.services.client.data.DeltaDataType
import ca.veltus.mindfulbreathingwearos.common.HeartRateResponse
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.DatabaseStatsDTO
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseUpdateEvent
import kotlinx.coroutines.flow.Flow

interface BreathingRepository {

    suspend fun getCapabilities(): Set<DeltaDataType<*, *>>

    fun heartRateMeasureFlow(): Flow<HeartRateResponse>

    fun getCacheStats(): Flow<Resource<DatabaseStatsDTO>>

    fun getDatabaseStats(): Flow<Resource<DatabaseStatsDTO>>

    fun getUncachedStats(): Flow<Resource<DatabaseStatsDTO>>

    fun clearJob()

    fun getDatabaseConnectionState(): Flow<Boolean>

    fun toggleDatabaseConnection(timerTime: Long)

    fun getTimerTimeMillis(): Flow<Long>

    fun getDatabaseUpdates(): Flow<DatabaseUpdateEvent>
}