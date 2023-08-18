package ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_count

import android.util.Log
import ca.veltus.mindfulbreathingwearos.common.Constants.TAG
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDatabaseCountUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    // For observing the value
    operator fun invoke(): Flow<Resource<Int>> = repository.getDatabaseItemCount()
}