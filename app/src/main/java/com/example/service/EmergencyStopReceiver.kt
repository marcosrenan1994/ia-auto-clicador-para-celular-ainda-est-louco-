package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Global Emergency Kill Switch Receiver.
 * Responds to system-level emergency stop intents from:
 * 1. Persistent Notification Action Button "[🚨 PARAR TUDO AGORA]"
 * 2. Hardware button shortcuts or quick settings
 * Instantly halts all automation jobs, gesture queues, and resets status to IDLE.
 */
class EmergencyStopReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_EMERGENCY_STOP = "com.example.ACTION_EMERGENCY_STOP"
        private const val TAG = "EmergencyStopReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_EMERGENCY_STOP) {
            Log.w(TAG, "EMERGENCY STOP TRIGGERED VIA GLOBAL RECEIVER!")
            OverlayBridge.sendCommand(OverlayBridge.OverlayCommand.EmergencyKillSwitch)
            OverlayBridge.automationStatus.value = com.example.viewmodel.AutomationStatus.IDLE
            OverlayBridge.clickCounter.value = 0
            AutoClickAccessibilityService.clearAllGestures()
        }
    }
}
