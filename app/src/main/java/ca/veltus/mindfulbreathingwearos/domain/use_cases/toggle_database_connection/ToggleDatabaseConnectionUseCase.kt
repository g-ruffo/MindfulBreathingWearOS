package ca.veltus.mindfulbreathingwearos.domain.use_cases.toggle_database_connection

import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleDatabaseConnectionUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    // For observing the value
    operator fun invoke(): Flow<Boolean> {
        return repository.getDatabaseConnectionState()
    }

    // For setting the value
    fun toggleDatabaseConnection(){
        repository.toggleDatabaseConnection()
    }
}