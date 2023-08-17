package ca.veltus.mindfulbreathingwearos.domain.use_cases.get_cache_count

import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCacheCountUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    // For observing the value
    operator fun invoke(): Flow<Resource<Int>> {
        return repository.getCacheItemCount()
    }
}