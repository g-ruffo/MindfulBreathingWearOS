package ca.veltus.mindfulbreathingwearos.common

import androidx.health.services.client.data.DataTypeAvailability
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.HeartRateDTO
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate

sealed class HeartRateResponse {
    class Availability(val availability: DataTypeAvailability) : HeartRateResponse()
    class Data(val heartRate: HeartRate) : HeartRateResponse()
    class Error(val message: String) : HeartRateResponse()
}