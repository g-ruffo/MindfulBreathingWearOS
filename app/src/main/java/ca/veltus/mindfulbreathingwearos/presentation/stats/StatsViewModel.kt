package ca.veltus.mindfulbreathingwearos.presentation.stats

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_cache_count.GetCacheCountUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_count.GetDatabaseCountUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_updates.GetDatabaseUpdatesUseCase
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
    private val getCacheItemCountUseCase: GetCacheCountUseCase,
    private val getDatabaseItemCountUseCase: GetDatabaseCountUseCase
) : ViewModel() {

    val isDatabaseConnected: StateFlow<Boolean> = toggleDatabaseConnectionUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    private val _cacheItemCount = MutableStateFlow<Resource<Int>>(Resource.Loading())
    val cacheItemCount: StateFlow<Resource<Int>> = _cacheItemCount

    private val _databaseItemCount = MutableStateFlow<Resource<Int>>(Resource.Loading())
    val databaseItemCount: StateFlow<Resource<Int>> = _databaseItemCount


    init {
        viewModelScope.launch {
            getDatabaseUpdatesUseCase().collect { value ->
                if (value.cacheUpdated) {
                    refreshCacheItemCount()
                }
                if (value.databaseUpdated) {
                    refreshDatabaseItemCount()
                }
            }
        }
    }

    private suspend fun refreshCacheItemCount() {
        getCacheItemCountUseCase.invoke().collect {
            _cacheItemCount.value = it
        }
    }

    private suspend fun refreshDatabaseItemCount() {
        getDatabaseItemCountUseCase.invoke().collect {
            _databaseItemCount.value = it
        }
    }
}