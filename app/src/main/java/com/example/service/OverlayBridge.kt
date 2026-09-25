package com.example.service

import com.example.model.PointerStyle
import com.example.model.TargetPoint
import com.example.viewmodel.ActionType
import com.example.viewmodel.AutomationStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton communication bridge connecting the UI/ViewModel with
 * the background FloatingOverlayService WindowManager views.
 * Enhanced with Microsoft Store auto-clicker features:
 * - 1000 CPS mode and live speed metrics
 * - Smart Visual Trigger scanning
 * - Macro recording and playback
 * - Multi-point on-screen draggable targets (1, 2, 3...)
 * - Quick profile switcher
 */
object OverlayBridge {

    sealed interface OverlayCommand {
        data object StartAutomation : OverlayCommand
        data object PauseAutomation : OverlayCommand
        data object StopAutomation : OverlayCommand
        data object PerformSingleClick : OverlayCommand
        data class UpdateTarget(val x: Int, val y: Int) : OverlayCommand
        data object TriggerAiConsultation : OverlayCommand
        data object ScrollDown : OverlayCommand
        data object ScrollUp : OverlayCommand
        data object CenterPointer : OverlayCommand
        data object TogglePointerVisibility : OverlayCommand
        data object ScanScreen : OverlayCommand
        data object ToggleAutonomousTrader : OverlayCommand
        data class MovePointerSmoothly(val startX: Int, val startY: Int, val endX: Int, val endY: Int, val durationMs: Long = 400L) : OverlayCommand
        data object CloseOverlay : OverlayCommand

        // Microsoft Store Clone Commands
        data object ToggleVisualTriggerScanner : OverlayCommand
        data object ToggleMacroRecording : OverlayCommand
        data object CycleProfileNext : OverlayCommand
        data object AddMultiPoint : OverlayCommand
        data object RemoveLastMultiPoint : OverlayCommand
        data class SetCpsRate(val cps: Int) : OverlayCommand
        data class SelectTargetPoint(val pointIndex: Int) : OverlayCommand

        // Live Voice & Emergency Kill Switch Commands
        data object EmergencyKillSwitch : OverlayCommand
        data object ToggleVoiceListening : OverlayCommand
        data object SwitchCameraFacing : OverlayCommand
    }

    // State mirrored from ViewModel to Overlay
    val automationStatus = MutableStateFlow(AutomationStatus.IDLE)
    val clickCounter = MutableStateFlow(0)
    val targetRepeat = MutableStateFlow(50)
    val clickInterval = MutableStateFlow(500L)
    val currentX = MutableStateFlow(540)
    val currentY = MutableStateFlow(960)
    val pointerVisible = MutableStateFlow(true)
    val pointerStyle = MutableStateFlow(PointerStyle.MOUSE_ARROW)
    val isOverlayActive = MutableStateFlow(false)

    // Live Voice Recognition & Emergency Safety States
    val lastVoiceCommandHeard = MutableStateFlow("")
    val isVoiceListeningActive = MutableStateFlow(false)
    val isTtsAudibleActive = MutableStateFlow(true)
    val isLiveCameraModeActive = MutableStateFlow(false)

    // Microsoft Store Cloned Features State
    val currentCps = MutableStateFlow(100)
    val activeProfileTitle = MutableStateFlow("⚡ Ultra CPS 1000 Gamer")
    val isVisualTriggerActive = MutableStateFlow(true)
    val isMacroRecording = MutableStateFlow(false)
    val multiPointsList = MutableStateFlow<List<TargetPoint>>(emptyList())
    val activeTargetPointIndex = MutableStateFlow(0)

    // Screen Vision & Forex Arbitrage Live State
    val aiStatusMessage = MutableStateFlow("IA Autônoma Pronta")
    val spreadTickerInfo = MutableStateFlow("USD/BRL 5.684 | Spread: +2.4¢")
    val detectedTargetLabel = MutableStateFlow("")
    val isAutonomousPatrolRunning = MutableStateFlow(false)

    // Flow for click impact animation triggers
    private val _clickPulseEvent = MutableSharedFlow<Long>(extraBufferCapacity = 10)
    val clickPulseEvent = _clickPulseEvent.asSharedFlow()

    // Commands sent from Floating Overlay to ViewModel
    private val _commands = MutableSharedFlow<OverlayCommand>(extraBufferCapacity = 10)
    val commands = _commands.asSharedFlow()

    fun emitClickPulse() {
        _clickPulseEvent.tryEmit(System.currentTimeMillis())
    }

    fun sendCommand(command: OverlayCommand) {
        _commands.tryEmit(command)
    }

    fun updateCoordinatesFromDrag(x: Int, y: Int) {
        currentX.value = x
        currentY.value = y
        sendCommand(OverlayCommand.UpdateTarget(x, y))
    }
}
