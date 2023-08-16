package ca.veltus.mindfulbreathingwearos.presentation.session

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor() : ViewModel() {
    private val _heartRate = mutableStateOf(72)
    val heartRate: State<Int> = _heartRate

    private val _sessionTimeRemaining = mutableStateOf(72)
    val sessionTimeRemaining: State<Int> = _sessionTimeRemaining

    fun startSession() {
        // Logic to start tracking heart rate
    }

    fun decrementSessionTime() {
        _sessionTimeRemaining.value--
    }

    fun endSession() {
        // Logic to compute session summary and possibly navigate to a summary screen
    }
}