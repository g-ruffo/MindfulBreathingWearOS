package ca.veltus.mindfulbreathingwearos.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseStats
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_cache_stats.GetCacheStatsUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_stats.GetDatabaseStatsUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_updates.GetDatabaseUpdatesUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_uncached_stats.GetUncachedStatsUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.toggle_database_connection.ToggleDatabaseConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getDatabaseUpdatesUseCase: GetDatabaseUpdatesUseCase,
    private val toggleDatabaseConnectionUseCase: ToggleDatabaseConnectionUseCase,
    private val getCacheStatsUseCase: GetCacheStatsUseCase,
    private val getDatabaseStatsUseCase: GetDatabaseStatsUseCase,
    private val getUncachedStatsUseCase: GetUncachedStatsUseCase
) : ViewModel() {

    val isDatabaseConnected: StateFlow<Boolean> = toggleDatabaseConnectionUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    private val _uncachedStats = MutableStateFlow<Resource<DatabaseStats>>(Resource.Loading())
    val uncachedStats: StateFlow<Resource<DatabaseStats>> = _uncachedStats

    private val _cacheStats = MutableStateFlow<Resource<DatabaseStats>>(Resource.Loading())
    val cacheStats: StateFlow<Resource<DatabaseStats>> = _cacheStats

    private val _databaseStats = MutableStateFlow<Resource<DatabaseStats>>(Resource.Loading())
    val databaseStats: StateFlow<Resource<DatabaseStats>> = _databaseStats


    init {
        viewModelScope.launch {
            getDatabaseUpdatesUseCase().collect { value ->
                if (value.uncachedUpdated) {
                    refreshUncachedStats()
                }
                if (value.cacheUpdated) {
                    refreshCacheStats()
                }
                if (value.databaseUpdated) {
                    refreshDatabaseStats()
                }
            }
        }
    }

    private suspend fun refreshUncachedStats() {
        getUncachedStatsUseCase.invoke().collect {
            _uncachedStats.value = it
        }
    }

    private suspend fun refreshCacheStats() {
        getCacheStatsUseCase.invoke().collect {
            _cacheStats.value = it
        }
    }

    private suspend fun refreshDatabaseStats() {
        getDatabaseStatsUseCase.invoke().collect {
            _databaseStats.value = it
        }
    }
}