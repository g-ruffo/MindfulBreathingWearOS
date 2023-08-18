package ca.veltus.mindfulbreathingwearos.domain.use_cases.get_cache_stats

import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.data.hardware.dto.toDatabaseStats
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseStats
import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCacheStatsUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    // Returns the number of cached items and the date it was last updated
    operator fun invoke(): Flow<Resource<DatabaseStats>> =
        repository.getCacheStats().map { resource ->
            when (resource) {
                is Resource.Success -> {
                    val domainModel = resource.data?.toDatabaseStats() ?: DatabaseStats(
                        count = -1,
                        lastAddedDate = null
                    )
                    Resource.Success(domainModel)
                }

                is Resource.Loading -> Resource.Loading()
                is Resource.Error -> Resource.Error(message = resource.message ?: "Error")
            }
        }
}