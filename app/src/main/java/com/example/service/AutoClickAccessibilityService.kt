package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Core Accessibility Service responsible for physically injecting touches,
 * taps, and gestures into any external app (Google Chrome, games, Android OS UI)
 * without requiring root access.
 * Enhanced with Hardware Volume Key Kill Switch and Emergency Halt protection.
 */
class AutoClickAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "AutoClickA11y"

        @Volatile
        var instance: AutoClickAccessibilityService? = null
            private set

        @Volatile
        var isEmergencyHalted = false
            private set

        fun clearAllGestures() {
            isEmergencyHalted = true
            Log.w(TAG, "EMERGENCY HALT: All physical touch dispatches are forcibly blocked.")
        }

        fun resumeGestures() {
            isEmergencyHalted = false
            Log.i(TAG, "Gestures resumed.")
        }

        private val _isServiceActive = MutableStateFlow(false)
        val isServiceActive = _isServiceActive.asStateFlow()

        /**
         * Dispatches a physical screen click at coordinates (x, y).
         * Works across all Android applications when this service is enabled in Android Settings.
         */
        fun clickAt(
            x: Float,
            y: Float,
            durationMs: Long = 45L,
            onSuccess: (() -> Unit)? = null,
            onFailure: (() -> Unit)? = null
        ): Boolean {
            if (isEmergencyHalted) {
                onFailure?.invoke()
                return false
            }

            val service = instance ?: return false
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return false

            val clickPath = Path().apply {
                moveTo(x, y)
            }

            val stroke = GestureDescription.StrokeDescription(
                clickPath,
                0L,
                durationMs.coerceIn(10L, 500L)
            )

            val gesture = GestureDescription.Builder()
                .addStroke(stroke)
                .build()

            return service.dispatchGesture(
                gesture,
                object : GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        super.onCompleted(gestureDescription)
                        onSuccess?.invoke()
                    }

                    override fun onCancelled(gestureDescription: GestureDescription?) {
                        super.onCancelled(gestureDescription)
                        Log.w(TAG, "Gesture at ($x, $y) cancelled by OS")
                        onFailure?.invoke()
                    }
                },
                null
            )
        }

        /**
         * Dispatches a continuous swipe gesture from (startX, startY) to (endX, endY).
         */
        fun swipe(
            startX: Float,
            startY: Float,
            endX: Float,
            endY: Float,
            durationMs: Long = 250L,
            onSuccess: (() -> Unit)? = null
        ): Boolean {
            if (isEmergencyHalted) return false
            val service = instance ?: return false
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return false

            val swipePath = Path().apply {
                moveTo(startX, startY)
                lineTo(endX, endY)
            }

            val stroke = GestureDescription.StrokeDescription(swipePath, 0L, durationMs)
            val gesture = GestureDescription.Builder().addStroke(stroke).build()

            return service.dispatchGesture(
                gesture,
                object : GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        super.onCompleted(gestureDescription)
                        onSuccess?.invoke()
                    }
                },
                null
            )
        }

        /**
         * Dispatches a physical vertical scroll down gesture (swipe up across apps).
         */
        fun scrollDown(
            screenWidth: Float = 1080f,
            screenHeight: Float = 1920f,
            onSuccess: (() -> Unit)? = null
        ): Boolean {
            val midX = screenWidth / 2f
            val startY = screenHeight * 0.72f
            val endY = screenHeight * 0.28f
            return swipe(midX, startY, midX, endY, 280L, onSuccess)
        }

        /**
         * Dispatches a physical vertical scroll up gesture (swipe down across apps).
         */
        fun scrollUp(
            screenWidth: Float = 1080f,
            screenHeight: Float = 1920f,
            onSuccess: (() -> Unit)? = null
        ): Boolean {
            val midX = screenWidth / 2f
            val startY = screenHeight * 0.28f
            val endY = screenHeight * 0.72f
            return swipe(midX, startY, midX, endY, 280L, onSuccess)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isServiceActive.value = true
        Log.i(TAG, "AutoClick Accessibility Service connected and ready to dispatch gestures.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Accessibility events can optionally be parsed for view hierarchy analysis
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false
        // Emergency Panic Killswitch: Volume Down or Volume Up stops all clicking loops immediately!
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP -> {
                    if (OverlayBridge.automationStatus.value != com.example.viewmodel.AutomationStatus.IDLE) {
                        Log.w(TAG, "HARDWARE VOLUME KEY PRESSED! Triggering emergency killswitch.")
                        clearAllGestures()
                        OverlayBridge.sendCommand(OverlayBridge.OverlayCommand.EmergencyKillSwitch)
                        OverlayBridge.automationStatus.value = com.example.viewmodel.AutomationStatus.IDLE
                        OverlayBridge.clickCounter.value = 0
                        return true // Consume key event for safety
                    }
                }
            }
        }
        return super.onKeyEvent(event)
    }

    override fun onInterrupt() {
        Log.w(TAG, "AutoClick Accessibility Service interrupted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
            _isServiceActive.value = false
        }
        Log.i(TAG, "AutoClick Accessibility Service destroyed.")
    }
}
