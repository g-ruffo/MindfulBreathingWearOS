package ca.veltus.mindfulbreathingwearos.domain.use_cases.get_timer_time

import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTimerTimeUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    operator fun invoke(): Flow<Int> = repository.getTimerTimeMillis().map { (it / 1000).toInt() }
}