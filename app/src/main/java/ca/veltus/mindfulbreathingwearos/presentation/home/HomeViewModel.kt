package ca.veltus.mindfulbreathingwearos.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _heartRate = mutableStateOf(72)
    val heartRate: State<Int> = _heartRate

    fun startBreathingSession() {
        // Logic to start session or navigate to another screen
    }

}