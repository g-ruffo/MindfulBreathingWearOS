package ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_updates

import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseUpdateEvent
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDatabaseUpdatesUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    // Returns which of the three data sources have been updated.
    operator fun invoke(): Flow<DatabaseUpdateEvent> = repository.getDatabaseUpdates()
}