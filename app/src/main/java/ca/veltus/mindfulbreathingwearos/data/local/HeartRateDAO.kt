package ca.veltus.mindfulbreathingwearos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.veltus.mindfulbreathingwearos.data.local.entity.HeartRateCacheEntity
import ca.veltus.mindfulbreathingwearos.data.local.entity.HeartRateEntity

@Dao
interface HeartRateDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHeartRates(heartRates: List<HeartRateEntity>)

    // Get the count of items in permanent storage
    @Query("SELECT COUNT(*) FROM heartrateentity")
    suspend fun getCountInPermanentStorage(): Int



    // Retrieve all from cache
    @Query("SELECT * FROM heartrateentity")
    suspend fun getAllFromCache(): List<HeartRateCacheEntity>

    @Query("DELETE FROM heartrateentity")
    suspend fun clearCache()

    // Get the count of items in cache
    @Query("SELECT COUNT(*) FROM heartrateentity")
    suspend fun getCountInCache(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHeartRateCache(heartRates: List<HeartRateCacheEntity>)
}