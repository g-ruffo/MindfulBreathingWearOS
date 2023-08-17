package ca.veltus.mindfulbreathingwearos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HeartRateCacheEntity(
    val value: Double,
    val timeInstant: Long,
    val accuracy: String?,
    @PrimaryKey(autoGenerate = true) val cacheId: Int = 0
)

fun HeartRateCacheEntity.toHeartRateEntity(): HeartRateEntity {
    return HeartRateEntity(
        value = this.value,
        timeInstant = this.timeInstant,
        accuracy = this.accuracy,
        id = this.cacheId
    )
}