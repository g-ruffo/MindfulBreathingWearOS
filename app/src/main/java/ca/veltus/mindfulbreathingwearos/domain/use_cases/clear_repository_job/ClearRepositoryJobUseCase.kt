package ca.veltus.mindfulbreathingwearos.domain.use_cases.clear_repository_job

import ca.veltus.mindfulbreathingwearos.domain.repository.BreathingRepository
import javax.inject.Inject

class ClearRepositoryJobUseCase @Inject constructor(
    private val repository: BreathingRepository
) {
    // Clears the repositories job when view model is destroyed
    operator fun invoke() {
        repository.clearJob()
    }
}