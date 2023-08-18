package ca.veltus.mindfulbreathingwearos.domain.use_cases.get_heart_rate

import ca.veltus.mindfulbreathingwearos.common.HeartRateResponse
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseStats
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetHeartRateUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    operator fun invoke(): Flow<HeartRateResponse> = repository.heartRateMeasureFlow()

}