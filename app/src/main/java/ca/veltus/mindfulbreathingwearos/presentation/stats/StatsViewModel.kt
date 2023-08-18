package ca.veltus.mindfulbreathingwearos.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseStats
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_cache_stats.GetCacheStatsUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_connection_state.GetDatabaseConnectionStateUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_stats.GetDatabaseStatsUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_updates.GetDatabaseUpdatesUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_timer_time.GetTimerTimeUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_uncached_stats.GetUncachedStatsUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.toggle_database_connection.ToggleDatabaseConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getDatabaseUpdatesUseCase: GetDatabaseUpdatesUseCase,
    private val getDatabaseConnectionStateUseCase: GetDatabaseConnectionStateUseCase,
    private val toggleDatabaseConnectionUseCase: ToggleDatabaseConnectionUseCase,
    private val getTimerTimeUseCase: GetTimerTimeUseCase,
    private val getCacheStatsUseCase: GetCacheStatsUseCase,
    private val getDatabaseStatsUseCase: GetDatabaseStatsUseCase,
    private val getUncachedStatsUseCase: GetUncachedStatsUseCase
) : ViewModel() {

    // Simulates the loss of connection to the database
    private val _isDatabaseConnected = MutableStateFlow(true)
    val isDatabaseConnected: StateFlow<Boolean> = _isDatabaseConnected

    // Value displayed to the user in the stepper
    private val _timerTimeSeconds = MutableStateFlow(TIMER_RESET_VALUE)
    val timerTimeSeconds: StateFlow<Int> = _timerTimeSeconds

    // A summary of the uncached data in the repository
    private val _uncachedStats = MutableStateFlow<Resource<DatabaseStats>>(Resource.Loading())
    val uncachedStats: StateFlow<Resource<DatabaseStats>> = _uncachedStats

    // A summary of the cached data in the database
    private val _cacheStats = MutableStateFlow<Resource<DatabaseStats>>(Resource.Loading())
    val cacheStats: StateFlow<Resource<DatabaseStats>> = _cacheStats

    // A summary of the saved data in the database
    private val _databaseStats = MutableStateFlow<Resource<DatabaseStats>>(Resource.Loading())
    val databaseStats: StateFlow<Resource<DatabaseStats>> = _databaseStats

    init {
        // Gets the previously saved values from the database when app first loads
        viewModelScope.launch {
            refreshDatabaseStats()
            refreshCacheStats()
        }
        // Updates whether the database has connection or not
        viewModelScope.launch {
            getDatabaseConnectionStateUseCase().collect { isConnected ->
                // If the database reconnects after timer has completed, reset the timers time to 4 minutes (240 seconds)
                if (isConnected) { _timerTimeSeconds.value = TIMER_RESET_VALUE }
                _isDatabaseConnected.value = isConnected
            }
        }
        // Collects the remaining time from the timers countdown after user disconnects the database
        viewModelScope.launch {
            getTimerTimeUseCase().collect { seconds ->
                _timerTimeSeconds.value = seconds
            }
        }
        // Collects a DatabaseUpdateEvent from the repository to determine which datasource has been changed
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
    // Called when user clicks the enable/ disable database button and passes the selected timer time to the repository.
    fun toggleDatabaseEnabled() {
        toggleDatabaseConnectionUseCase(timerTime = timerTimeSeconds.value)
    }
    // Called when either the plus or minus stepper button is pressed. Each call passes +/- 60 seconds
    fun updateTimerTime(seconds: Int) {
        _timerTimeSeconds.value += seconds
    }

    companion object {
        // The default reset timer value of 4 minutes in seconds
        private const val TIMER_RESET_VALUE = 240
    }
}