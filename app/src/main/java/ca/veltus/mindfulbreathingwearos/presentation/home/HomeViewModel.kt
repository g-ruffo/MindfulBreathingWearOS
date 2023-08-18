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
import ca.veltus.mindfulbreathingwearos.common.UIState
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate
import ca.veltus.mindfulbreathingwearos.domain.use_cases.clear_repository_job.ClearRepositoryJobUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.get_heart_rate.GetHeartRateUseCase
import ca.veltus.mindfulbreathingwearos.domain.use_cases.has_heart_rate_sensor.HasHeartRateSensorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val hasHeartRateSensorUseCase: HasHeartRateSensorUseCase,
    private val getHeartRateUseCase: GetHeartRateUseCase,
    private val clearRepositoryJobUseCase: ClearRepositoryJobUseCase
) : ViewModel() {

    // If device does not have available sensor variable is set as false
    private val _hasHeartRateSensor = MutableStateFlow(false)
    private val hasHeartRateSensor: StateFlow<Boolean> = _hasHeartRateSensor

    // Enables the collection of heart rate data
    private val _enabled: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val enabled: StateFlow<Boolean> = _enabled

    // The latest heart rate data retrieved from the repository
    private val _heartRate = mutableStateOf<HeartRate?>(null)
    val heartRate: State<HeartRate?> = _heartRate

    // TODO: Implement sensor availability state
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
            // If enabled, begin collecting heart rate data
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
        // Call the clear job method of the BreathingRepository
        clearRepositoryJobUseCase()
    }

    // Called from UI to begin retrieving heart rate data
    fun enableHeartRate(isEnabled: Boolean) {
        _enabled.value = isEnabled
        if (!isEnabled) {
            _availability.value = DataTypeAvailability.UNKNOWN
        }
    }
}