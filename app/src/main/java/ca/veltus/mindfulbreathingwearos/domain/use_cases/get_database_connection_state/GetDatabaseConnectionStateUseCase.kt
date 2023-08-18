package ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_connection_state

import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDatabaseConnectionStateUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    // Returns the connection status of the database to the view model
    operator fun invoke(): Flow<Boolean> = repository.getDatabaseConnectionState()
}