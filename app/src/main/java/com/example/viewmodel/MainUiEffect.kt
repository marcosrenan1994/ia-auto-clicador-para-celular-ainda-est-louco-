package com.example.viewmodel

sealed interface MainUiEffect {
    data class ShowSnackbar(val message: String) : MainUiEffect
    data class ShowToast(val message: String) : MainUiEffect
    data object TriggerHaptic : MainUiEffect
    data object ScrollToLatestLog : MainUiEffect
    data object RequestOverlayPermission : MainUiEffect
    data object OpenAccessibilitySettings : MainUiEffect
    data class ClickImpactPulse(val x: Int, val y: Int) : MainUiEffect
    data class ExecuteBrowserJs(val script: String) : MainUiEffect
}
