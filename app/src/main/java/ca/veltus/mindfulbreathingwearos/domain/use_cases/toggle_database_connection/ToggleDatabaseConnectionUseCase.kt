package ca.veltus.mindfulbreathingwearos.domain.use_cases.toggle_database_connection

import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import javax.inject.Inject

class ToggleDatabaseConnectionUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    // Toggles the database connection state.
    operator fun invoke(timerTime: Int) {
        // Convert the view models seconds Int to milliseconds Long
        val timeInMillis = (timerTime * 1000).toLong()
        repository.toggleDatabaseConnection(timerTime = timeInMillis)
    }
}