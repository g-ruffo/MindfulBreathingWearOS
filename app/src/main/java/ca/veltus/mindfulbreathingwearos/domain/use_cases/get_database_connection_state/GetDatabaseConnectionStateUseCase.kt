package ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_connection_state

import androidx.health.services.client.data.DataType
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDatabaseConnectionStateUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.getDatabaseConnectionState()
}