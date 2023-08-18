package ca.veltus.mindfulbreathingwearos.domain.use_cases.get_heart_rate

import ca.veltus.mindfulbreathingwearos.common.HeartRateResponse
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHeartRateUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    // Returns the most recent heart rate data to the view controller
    operator fun invoke(): Flow<HeartRateResponse> = repository.heartRateMeasureFlow()

}