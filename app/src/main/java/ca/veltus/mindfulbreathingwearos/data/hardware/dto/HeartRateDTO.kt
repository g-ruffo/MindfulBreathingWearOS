package ca.veltus.mindfulbreathingwearos.data.hardware.dto

import android.os.SystemClock
import androidx.health.services.client.data.DataPointAccuracy
import androidx.health.services.client.data.HeartRateAccuracy
import androidx.health.services.client.data.SampleDataPoint
import ca.veltus.mindfulbreathingwearos.data.local.entity.HeartRateCacheEntity
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate
import java.time.Instant

data class HeartRateDTO(
    val value: Double,
    val timeInstant: Long,
    val accuracy: String?
)

fun HeartRateDTO.toHeartRateCacheEntity(): HeartRateCacheEntity {
    return HeartRateCacheEntity(
        value = this.value,
        timeInstant = this.timeInstant,
        accuracy = this.accuracy
    )
}

fun HeartRateDTO.toHeartRate(): HeartRate {
    return HeartRate(
        value = value.toInt()
    )
}

fun SampleDataPoint<Double>.toDTO(): HeartRateDTO {
    // Calculate the boot instant
    val bootInstant = Instant.ofEpochMilli(System.currentTimeMillis() - SystemClock.elapsedRealtime())

    // Convert timeDurationFromBoot to actual time instant
    val dataPointInstant = this.getTimeInstant(bootInstant)

    val heartRateAccuracy = determineHeartRateAccuracy(this.accuracy)

    return HeartRateDTO(
        value = this.value,
        timeInstant = dataPointInstant.toEpochMilli(),
        accuracy = heartRateAccuracy
    )
}

fun List<SampleDataPoint<Double>>.toDTOList(): List<HeartRateDTO> {
    return this.map { it.toDTO() }
}

private fun determineHeartRateAccuracy(accuracy: DataPointAccuracy?): String {
    return when (accuracy) {
        is HeartRateAccuracy -> accuracy.sensorStatus.name
        else -> "UNKNOWN"
    }
}
