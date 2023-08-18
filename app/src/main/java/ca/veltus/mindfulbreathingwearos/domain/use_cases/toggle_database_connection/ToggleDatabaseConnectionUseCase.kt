package ca.veltus.mindfulbreathingwearos.domain.use_cases.toggle_database_connection

import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleDatabaseConnectionUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    operator fun invoke(timerTime: Int) {
        val timeInMillis = (timerTime * 1000).toLong()
        repository.toggleDatabaseConnection(timerTime = timeInMillis)
    }
}