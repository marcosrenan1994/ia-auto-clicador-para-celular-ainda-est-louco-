package com.example.data

import com.example.model.AutoClickerProfile
import com.example.model.ClickRateMode
import com.example.model.ExtendedClickType
import com.example.model.MacroSequence
import com.example.model.MouseButtonType
import com.example.model.SmartVisualTriggerRule
import com.example.model.TargetPoint
import com.example.service.AutoClickAccessibilityService
import com.example.viewmodel.ActionType
import com.example.viewmodel.MainUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * High-performance coordinator for Microsoft Store auto-clicker features:
 * - 1000 CPS Ultra High-Speed Engine
 * - Rolling CPS Real-time Meter
 * - Smart Visual Trigger continuous scanner
 * - Macro Playback and Recording Sequencer
 * - Multi-Point Target cycling
 * - Unlimited Profile Manager
 */
class MicrosoftClickerManager(
    private val scope: CoroutineScope,
    val visualTriggerEngine: SmartVisualTriggerEngine = SmartVisualTriggerEngine(),
    val macroEngine: MacroEngine = MacroEngine()
) {

    private val clickTimestamps = ConcurrentLinkedQueue<Long>()
    private var visualScannerJob: Job? = null
    private var liveCpsJob: Job? = null
    private var macroPlaybackJob: Job? = null

    private val _measuredCps = MutableStateFlow(0.0)
    val measuredCps = _measuredCps.asStateFlow()

    init {
        startCpsMeasurementTicker()
    }

    private fun startCpsMeasurementTicker() {
        liveCpsJob?.cancel()
        liveCpsJob = scope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                // Purge timestamps older than 1000ms
                while (clickTimestamps.isNotEmpty() && (now - (clickTimestamps.peek() ?: 0L)) > 1000L) {
                    clickTimestamps.poll()
                }
                _measuredCps.value = clickTimestamps.size.toDouble()
                delay(200L)
            }
        }
    }

    fun recordClickTimestamp() {
        clickTimestamps.add(System.currentTimeMillis())
    }

    /**
     * Calculates delay in milliseconds based on whether user selected CPS (1 - 1000)
     * or Exact Time (Hours, Minutes, Seconds, Milliseconds).
     */
    fun calculateTargetIntervalMs(state: MainUiState): Long {
        return when (state.rateMode) {
            ClickRateMode.CPS -> {
                val safeCps = state.targetCps.coerceIn(1, 1000)
                (1000L / safeCps).coerceAtLeast(1L)
            }
            ClickRateMode.EXACT_TIME -> {
                val totalMs = (state.intervalHours * 3600000L) +
                        (state.intervalMinutes * 60000L) +
                        (state.intervalSeconds * 1000L) +
                        state.intervalMillis
                totalMs.coerceAtLeast(1L)
            }
        }
    }

    /**
     * Applies humanized coordinate jitter (anti-detection radius) around (x, y).
     */
    fun computeJitteredCoordinates(x: Int, y: Int, radiusPx: Int): Pair<Int, Int> {
        if (radiusPx <= 0) return Pair(x, y)

        val angle = Random.nextDouble(0.0, 2.0 * Math.PI)
        val distance = Random.nextDouble(0.0, radiusPx.toDouble())
        val jitterX = (x + distance * cos(angle)).toInt().coerceAtLeast(0)
        val jitterY = (y + distance * sin(angle)).toInt().coerceAtLeast(0)
        return Pair(jitterX, jitterY)
    }

    /**
     * Executes physical clicks corresponding to click type and button.
     * Supports high-speed 1000 CPS burst execution.
     */
    fun executeClickGesture(
        x: Int,
        y: Int,
        clickType: ExtendedClickType,
        mouseButton: MouseButtonType,
        holdMs: Long = 40L
    ): Boolean {
        recordClickTimestamp()

        return when (clickType) {
            ExtendedClickType.SINGLE -> {
                AutoClickAccessibilityService.clickAt(x.toFloat(), y.toFloat(), durationMs = holdMs)
            }
            ExtendedClickType.DOUBLE -> {
                val first = AutoClickAccessibilityService.clickAt(x.toFloat(), y.toFloat(), durationMs = 25L)
                recordClickTimestamp()
                AutoClickAccessibilityService.clickAt(x.toFloat(), y.toFloat(), durationMs = 25L)
                first
            }
            ExtendedClickType.TRIPLE -> {
                AutoClickAccessibilityService.clickAt(x.toFloat(), y.toFloat(), durationMs = 20L)
                recordClickTimestamp()
                AutoClickAccessibilityService.clickAt(x.toFloat(), y.toFloat(), durationMs = 20L)
                recordClickTimestamp()
                AutoClickAccessibilityService.clickAt(x.toFloat(), y.toFloat(), durationMs = 20L)
                true
            }
            ExtendedClickType.HOLD_PRESS -> {
                AutoClickAccessibilityService.clickAt(x.toFloat(), y.toFloat(), durationMs = holdMs.coerceAtLeast(300L))
            }
        }
    }

    /**
     * Dispatches an ultra-high-speed burst of clicks (used when CPS >= 200 or in 1000 CPS mode).
     */
    fun executeUltraSpeedBurst(
        x: Int,
        y: Int,
        burstCount: Int = 10,
        clickType: ExtendedClickType = ExtendedClickType.SINGLE,
        mouseButton: MouseButtonType = MouseButtonType.LEFT
    ): Int {
        var delivered = 0
        for (i in 1..burstCount) {
            recordClickTimestamp()
            val ok = AutoClickAccessibilityService.clickAt(x.toFloat(), y.toFloat(), durationMs = 12L)
            if (ok) delivered++
        }
        return delivered
    }

    /**
     * Continuous Visual Trigger Scanner loop.
     */
    fun startVisualTriggerScanner(
        onTriggerFired: (rule: SmartVisualTriggerRule, x: Int, y: Int) -> Unit
    ) {
        visualScannerJob?.cancel()
        visualTriggerEngine.setScanning(true)
        visualScannerJob = scope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val events = visualTriggerEngine.tickScanCycle(now)
                for (event in events) {
                    onTriggerFired(event.rule, event.detectedX, event.detectedY)
                }
                delay(120L)
            }
        }
    }

    fun stopVisualTriggerScanner() {
        visualScannerJob?.cancel()
        visualTriggerEngine.setScanning(false)
    }

    /**
     * Executes a Macro sequence.
     */
    fun playMacroSequence(
        macro: MacroSequence,
        onStepUpdated: (stepIndex: Int) -> Unit,
        onCompleted: () -> Unit
    ) {
        macroPlaybackJob?.cancel()
        macroPlaybackJob = scope.launch {
            val speedFactor = macro.playbackSpeed.coerceIn(0.5f, 10.0f)
            var currentIteration = 0
            val maxLoops = if (macro.loopUntilStopped || macro.repeatCount <= 0) Int.MAX_VALUE else macro.repeatCount

            while (isActive && currentIteration < maxLoops) {
                currentIteration++
                for (step in macro.steps) {
                    if (!isActive) break
                    onStepUpdated(step.stepIndex)
                    val effectiveDelay = (step.delayBeforeMs / speedFactor).toLong().coerceAtLeast(10L)
                    delay(effectiveDelay)

                    when (step.type) {
                        com.example.model.MacroStepType.CLICK -> {
                            executeClickGesture(step.x, step.y, ExtendedClickType.SINGLE, MouseButtonType.LEFT, step.durationMs)
                        }
                        com.example.model.MacroStepType.DOUBLE_CLICK -> {
                            executeClickGesture(step.x, step.y, ExtendedClickType.DOUBLE, MouseButtonType.LEFT, step.durationMs)
                        }
                        com.example.model.MacroStepType.LONG_PRESS -> {
                            executeClickGesture(step.x, step.y, ExtendedClickType.HOLD_PRESS, MouseButtonType.LEFT, step.durationMs)
                        }
                        com.example.model.MacroStepType.SWIPE -> {
                            AutoClickAccessibilityService.swipe(
                                step.x.toFloat(),
                                step.y.toFloat(),
                                step.endX.toFloat(),
                                step.endY.toFloat(),
                                durationMs = (step.durationMs / speedFactor).toLong().coerceAtLeast(100L)
                            )
                        }
                        com.example.model.MacroStepType.WAIT_DELAY -> {
                            // Already waited before
                        }
                        com.example.model.MacroStepType.KEY_INPUT,
                        com.example.model.MacroStepType.VISUAL_CHECK -> {
                            // Click at target for input focus
                            executeClickGesture(step.x, step.y, ExtendedClickType.SINGLE, MouseButtonType.LEFT)
                        }
                    }
                }
            }
            onStepUpdated(-1)
            onCompleted()
        }
    }

    fun stopMacroPlayback() {
        macroPlaybackJob?.cancel()
        macroEngine.resetPlaybackState()
    }
}
