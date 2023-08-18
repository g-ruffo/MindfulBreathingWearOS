package ca.veltus.mindfulbreathingwearos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.DatabaseStatsDTO
import ca.veltus.mindfulbreathingwearos.data.local.entity.HeartRateCacheEntity
import ca.veltus.mindfulbreathingwearos.data.local.entity.HeartRateEntity
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseStats

@Dao
interface HeartRateDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHeartRates(heartRates: List<HeartRateEntity>)

    // Combined function to get the count of items in the database and the last added timestamp
    @Query("SELECT COUNT(*) as count, MAX(timeInstant) as lastAddedTimestamp FROM heartrateentity")
    suspend fun getDatabaseStats(): DatabaseStatsDTO



    // Retrieve all from cache
    @Query("SELECT * FROM heartratecacheentity")
    suspend fun getAllFromCache(): List<HeartRateCacheEntity>

    @Query("DELETE FROM heartratecacheentity")
    suspend fun clearCache()

    // Combined function to get the count of items in cache and the last added timestamp
    @Query("SELECT COUNT(*) as count, MAX(timeInstant) as lastAddedTimestamp FROM heartratecacheentity")
    suspend fun getCacheStats(): DatabaseStatsDTO

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHeartRateCache(heartRates: List<HeartRateCacheEntity>)
}