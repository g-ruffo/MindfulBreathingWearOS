package ca.veltus.mindfulbreathingwearos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate

@Entity
data class HeartRateEntity(
    val value: Double,
    val timeInstant: Long,
    val accuracy: String?,
    @PrimaryKey val id: Int
)

