package com.example.model

import com.example.viewmodel.ActionType

enum class PointerStyle(val displayName: String) {
    MOUSE_ARROW("Seta de Mouse"),
    TACTICAL_CROSSHAIR("Mira Tática"),
    CYBER_RETICLE("Retículo Cyber"),
    PULSE_BULLSEYE("Alvo de Precisão")
}

data class ScientificCalculation(
    val title: String,
    val formula: String,
    val explanation: String,
    val value: String
)

data class AutonomousAction(
    val id: Int,
    val name: String,
    val targetX: Int,
    val targetY: Int,
    val clicks: Int,
    val intervalMs: Long,
    val delayAfterMs: Long,
    val actionType: ActionType = ActionType.SINGLE_TAP
)

data class ScientistAnalysis(
    val explanation: String,
    val recommendedIntervalMs: Long,
    val recommendedJitterMs: Long,
    val theoreticalCps: Double,
    val dropRateEstimate: String? = null,
    val safetyScore: String = "98% (Anti-detecção)",
    val calculations: List<ScientificCalculation> = emptyList(),
    val autonomousPlan: List<AutonomousAction> = emptyList(),
    val sources: List<String> = emptyList()
)
