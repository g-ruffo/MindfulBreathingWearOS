package ca.veltus.mindfulbreathingwearos.domain.use_cases.has_heart_rate_sensor

import androidx.health.services.client.data.DataType
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HasHeartRateSensorUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    operator fun invoke(): Flow<Boolean> = flow {
        emit((DataType.HEART_RATE_BPM in repository.getCapabilities()))
    }
}