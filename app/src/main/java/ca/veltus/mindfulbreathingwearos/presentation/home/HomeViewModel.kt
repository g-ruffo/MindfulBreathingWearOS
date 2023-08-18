package ca.veltus.mindfulbreathingwearos.presentation.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.health.services.client.data.DataTypeAvailability
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.veltus.mindfulbreathingwearos.common.Constants.TAG
import ca.veltus.mindfulbreathingwearos.common.HeartRateResponse
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate
import ca.veltus.mindfulbreathingwearos.domain.use_cases.clear_repository_job.ClearRepositoryJobUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_cache_count.GetCacheCountUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_count.GetDatabaseCountUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_database_updates.GetDatabaseUpdatesUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_heart_rate.GetHeartRateUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.has_heart_rate_sensor.HasHeartRateSensorUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.toggle_database_connection.ToggleDatabaseConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val hasHeartRateSensorUseCase: HasHeartRateSensorUseCase,
    private val getHeartRateUseCase: GetHeartRateUseCase,
    private val clearRepositoryJobUseCase: ClearRepositoryJobUseCase
    ) : ViewModel() {

    // Define a MutableStateFlow to hold the boolean value.
    private val _hasHeartRateSensor = MutableStateFlow(false)
    val hasHeartRateSensor: StateFlow<Boolean> = _hasHeartRateSensor

    private val _enabled: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val enabled: StateFlow<Boolean> = _enabled

    private val _heartRate = mutableStateOf<HeartRate?>(null)
    val heartRate: State<HeartRate?> = _heartRate

    private val _availability: MutableState<DataTypeAvailability> =
        mutableStateOf(DataTypeAvailability.UNKNOWN)
    val availability: State<DataTypeAvailability> = _availability

    private val _uiState: MutableState<UIState> = mutableStateOf(UIState.Startup)
    val uiState: State<UIState> = _uiState
    init {
        viewModelScope.launch {
            hasHeartRateSensorUseCase().collect { value ->
                _hasHeartRateSensor.value = value
                _uiState.value = if (hasHeartRateSensor.value) {
                    UIState.Supported
                } else {
                    UIState.NotSupported
                }
            }
        }

        viewModelScope.launch {
            enabled.collect {
                if (it) {
                    getHeartRateUseCase()
                        .takeWhile { enabled.value }
                        .collect { measureMessage ->
                            when (measureMessage) {
                                is HeartRateResponse.Data -> {
                                    _heartRate.value = measureMessage.heartRate
                                }
                                is HeartRateResponse.Availability -> {
                                    _availability.value = measureMessage.availability
                                }
                                is HeartRateResponse.Error -> {
                                    Log.e(TAG, ": ${measureMessage.message}")
                                }
                            }
                        }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Call the clear method of the BreathingRepository
        clearRepositoryJobUseCase()
    }

    fun enableHeartRate(isEnabled: Boolean) {
        _enabled.value = isEnabled
        if (!isEnabled) {
            _availability.value = DataTypeAvailability.UNKNOWN
        }
    }
}

    sealed class UIState {
        object Startup : UIState()
        object NotSupported : UIState()
        object Supported : UIState()
    }