package ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_updates

import android.util.Log
import ca.veltus.mindfulbreathingwearos.common.Constants
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseUpdateEvent
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDatabaseUpdatesUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    // For observing the value
    operator fun invoke(): Flow<DatabaseUpdateEvent> = repository.getDatabaseUpdates()
}