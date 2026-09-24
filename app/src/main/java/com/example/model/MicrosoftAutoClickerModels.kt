package com.example.model

import com.example.viewmodel.ActionType

/**
 * Click Rate Mode: whether user defines speed via Clicks Per Second (1 - 1000 CPS)
 * or via precise time intervals (Hours, Minutes, Seconds, Milliseconds).
 * Cloned directly from Microsoft Store Auto Clicker apps (9mszwlljjvb1 / 9n31lzkvgqvp).
 */
enum class ClickRateMode(val displayName: String) {
    CPS("Velocidade CPS (1 a 1000 Cliques/seg)"),
    EXACT_TIME("Intervalo de Tempo Exato (H / M / S / ms)")
}

/**
 * Mouse button simulated during click execution.
 */
enum class MouseButtonType(val displayName: String) {
    LEFT("Botão Esquerdo (Principal)"),
    RIGHT("Botão Direito (Menu de Contexto)"),
    MIDDLE("Botão do Meio (Scroll / Roda)")
}

/**
 * Extended click types supported by Microsoft Store auto clicker.
 */
enum class ExtendedClickType(val displayName: String) {
    SINGLE("Clique Simples"),
    DOUBLE("Clique Duplo"),
    TRIPLE("Clique Triplo (Marcação Rápida)"),
    HOLD_PRESS("Manter Pressionado (Hold)")
}

/**
 * Draggable numbered target point for Multi-Point overlay execution.
 */
data class TargetPoint(
    val id: Int, // 1, 2, 3...
    val x: Int,
    val y: Int,
    val delayAfterMs: Long = 250L,
    val holdDurationMs: Long = 40L,
    val clickCount: Int = 1,
    val label: String = "Ponto $id",
    val colorHex: String = "#00E5FF" // Neon cyan default
)

/**
 * Visual condition for Smart Visual Triggers (Image & Color Recognition).
 */
enum class VisualTriggerCondition(val displayName: String) {
    COLOR_MATCHES("Quando a Cor Exata Aparecer"),
    COLOR_CHANGES("Quando a Cor Mudar / Desaparecer"),
    LUMINANCE_SPIKE("Quando Houver Clarão / Flash de Brilho"),
    IMAGE_PATTERN_DETECTED("Quando Padrão Visual For Reconhecido")
}

/**
 * Action executed when a Visual Trigger fires.
 */
enum class VisualTriggerAction(val displayName: String) {
    CLICK_AT_TRIGGER_POINT("Clicar Imediatamente no Ponto Detectado"),
    BURST_1000_CPS("Disparar Rajada Turbo de 1000 CPS"),
    EXECUTE_MACRO("Executar Sequência de Macro Completa"),
    NOTIFY_QUANTUM_COUNCIL("Enviar Alerta para a Sala Quântica de Cientistas"),
    PAUSE_AUTOMATION("Pausar Automação com Segurança")
}

/**
 * Smart Visual Trigger Rule (Image / Color Recognition Engine).
 */
data class SmartVisualTriggerRule(
    val id: String,
    val name: String,
    val sampleX: Int,
    val sampleY: Int,
    val targetColorHex: String = "#10B981", // Emerald green default
    val tolerancePercent: Int = 85, // 0 - 100%
    val condition: VisualTriggerCondition = VisualTriggerCondition.COLOR_MATCHES,
    val action: VisualTriggerAction = VisualTriggerAction.CLICK_AT_TRIGGER_POINT,
    val checkIntervalMs: Long = 150L,
    val cooldownMs: Long = 800L,
    val isEnabled: Boolean = true,
    val triggerCount: Int = 0,
    val lastTriggeredTimestamp: Long = 0L
)

/**
 * Types of actions recorded inside a macro sequence.
 */
enum class MacroStepType(val displayName: String) {
    CLICK("Clique"),
    DOUBLE_CLICK("Clique Duplo"),
    LONG_PRESS("Segurar Pressionado"),
    SWIPE("Arrastar / Deslizar"),
    WAIT_DELAY("Pausa / Aguardar"),
    KEY_INPUT("Digitar Texto / Tecla"),
    VISUAL_CHECK("Verificação de Gatilho Visual")
}

typealias MacroActionType = MacroStepType

/**
 * Individual step inside a Macro.
 */
data class MacroStep(
    val stepIndex: Int,
    val type: MacroStepType,
    val x: Int = 540,
    val y: Int = 960,
    val endX: Int = 540,
    val endY: Int = 500,
    val delayBeforeMs: Long = 200L,
    val durationMs: Long = 50L,
    val textPayload: String = "",
    val note: String = ""
)

/**
 * Complete Macro Sequence (Keyboard & Mouse Macro Recorder).
 */
data class MacroSequence(
    val id: String,
    val name: String,
    val description: String,
    val steps: List<MacroStep>,
    val repeatCount: Int = 0, // 0 = infinite loop
    val playbackSpeed: Float = 1.0f, // 0.5x, 1x, 2x, 5x, 10x
    val loopUntilStopped: Boolean = true
)

/**
 * Profile Category.
 */
enum class ProfileCategory(val displayName: String) {
    GAMING("Jogos (Roblox / Minecraft / RPG)"),
    TRADING("Câmbio & Arbitragem Financeira"),
    DATA_ENTRY("Automação de Dados & Formulários"),
    STEALTH("Anti-Detecção & Modo Humano"),
    CUSTOM("Personalizado do Usuário")
}

/**
 * Full Auto Clicker Profile (Unlimited Profiles Management).
 */
data class AutoClickerProfile(
    val id: String,
    val title: String,
    val description: String,
    val category: ProfileCategory,
    val iconEmoji: String,
    val rateMode: ClickRateMode = ClickRateMode.CPS,
    val targetCps: Int = 100, // 1 - 1000 CPS
    val intervalHours: Int = 0,
    val intervalMinutes: Int = 0,
    val intervalSeconds: Int = 0,
    val intervalMillis: Long = 10L,
    val extendedClickType: ExtendedClickType = ExtendedClickType.SINGLE,
    val mouseButton: MouseButtonType = MouseButtonType.LEFT,
    val jitterEnabled: Boolean = true,
    val jitterMs: Long = 15L,
    val coordinateJitterRadiusPx: Int = 4,
    val multiPoints: List<TargetPoint> = emptyList(),
    val macroId: String? = null,
    val visualTriggerId: String? = null,
    val isBuiltIn: Boolean = false
)
