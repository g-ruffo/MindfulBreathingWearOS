package ca.veltus.mindfulbreathingwearos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ca.veltus.mindfulbreathingwearos.data.local.entity.HeartRateCacheEntity
import ca.veltus.mindfulbreathingwearos.data.local.entity.HeartRateEntity

@Database(
    entities = [HeartRateEntity::class, HeartRateCacheEntity::class],
    version = 1
)
abstract class HeartRateDatabase: RoomDatabase() {

    abstract val dao: HeartRateDAO
}