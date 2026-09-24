package com.example.data

import com.example.model.MacroActionType
import com.example.model.MacroSequence
import com.example.model.MacroStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Macro Recorder & Playback Sequencer Engine cloned from Microsoft Store auto clicker (9mszwlljjvb1 / 9n31lzkvgqvp).
 * Supports keyboard/mouse recording, multi-step playback, speed scaling (up to 10x Turbo),
 * and looping configurations.
 */
class MacroEngine {

    private val _macros = MutableStateFlow<List<MacroSequence>>(
        MicrosoftProfilePresets.getDefaultMacros()
    )
    val macros = _macros.asStateFlow()

    private val _selectedMacro = MutableStateFlow<MacroSequence?>(
        MicrosoftProfilePresets.getDefaultMacros().firstOrNull()
    )
    val selectedMacro = _selectedMacro.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _recordedSteps = MutableStateFlow<List<MacroStep>>(emptyList())
    val recordedSteps = _recordedSteps.asStateFlow()

    private val _currentPlayingStep = MutableStateFlow<Int>(-1)
    val currentPlayingStep = _currentPlayingStep.asStateFlow()

    private var recordStartTime = 0L
    private var lastRecordedTime = 0L

    fun startRecording() {
        _isRecording.value = true
        _recordedSteps.value = emptyList()
        recordStartTime = System.currentTimeMillis()
        lastRecordedTime = recordStartTime
    }

    fun recordClickStep(x: Int, y: Int, type: MacroActionType = MacroActionType.CLICK) {
        if (!_isRecording.value) return
        val now = System.currentTimeMillis()
        val delaySinceLast = (now - lastRecordedTime).coerceIn(50L, 5000L)
        lastRecordedTime = now

        val nextIndex = _recordedSteps.value.size + 1
        val newStep = MacroStep(
            stepIndex = nextIndex,
            type = type,
            x = x,
            y = y,
            delayBeforeMs = delaySinceLast,
            durationMs = 45L,
            note = "Toque gravado #${nextIndex} em ($x, $y)"
        )
        _recordedSteps.value = _recordedSteps.value + newStep
    }

    fun recordSwipeStep(startX: Int, startY: Int, endX: Int, endY: Int, durationMs: Long = 250L) {
        if (!_isRecording.value) return
        val now = System.currentTimeMillis()
        val delaySinceLast = (now - lastRecordedTime).coerceIn(50L, 5000L)
        lastRecordedTime = now

        val nextIndex = _recordedSteps.value.size + 1
        val newStep = MacroStep(
            stepIndex = nextIndex,
            type = MacroActionType.SWIPE,
            x = startX,
            y = startY,
            endX = endX,
            endY = endY,
            delayBeforeMs = delaySinceLast,
            durationMs = durationMs,
            note = "Arrasto #${nextIndex} de ($startX, $startY) até ($endX, $endY)"
        )
        _recordedSteps.value = _recordedSteps.value + newStep
    }

    fun stopRecordingAndSave(name: String, description: String = "Macro gravado pelo usuário"): MacroSequence? {
        _isRecording.value = false
        val steps = _recordedSteps.value
        if (steps.isEmpty()) return null

        val macro = MacroSequence(
            id = "macro_${System.currentTimeMillis()}",
            name = if (name.isNotBlank()) name else "Macro Personalizado ${_macros.value.size + 1}",
            description = description,
            steps = steps,
            repeatCount = 0,
            playbackSpeed = 1.0f,
            loopUntilStopped = true
        )
        _macros.value = _macros.value + macro
        _selectedMacro.value = macro
        _recordedSteps.value = emptyList()
        return macro
    }

    fun cancelRecording() {
        _isRecording.value = false
        _recordedSteps.value = emptyList()
    }

    fun selectMacro(macro: MacroSequence) {
        _selectedMacro.value = macro
    }

    fun updatePlaybackStep(stepIndex: Int) {
        _currentPlayingStep.value = stepIndex
    }

    fun resetPlaybackState() {
        _currentPlayingStep.value = -1
    }

    fun deleteMacro(macroId: String) {
        _macros.value = _macros.value.filter { it.id != macroId }
        if (_selectedMacro.value?.id == macroId) {
            _selectedMacro.value = _macros.value.firstOrNull()
        }
    }
}
