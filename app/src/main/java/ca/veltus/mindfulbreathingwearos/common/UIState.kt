package ca.veltus.mindfulbreathingwearos.common

sealed class UIState {
    object Startup : UIState()
    object NotSupported : UIState()
    object Supported : UIState()
}