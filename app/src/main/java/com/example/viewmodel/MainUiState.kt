package com.example.viewmodel

import com.example.data.MicrosoftProfilePresets
import com.example.data.forex.ArbitrageOpportunity
import com.example.data.forex.CurrencyQuote
import com.example.data.local.entity.ArbitrageSignalEntity
import com.example.data.local.entity.AutonomousActionEntity
import com.example.data.local.entity.ScreenMemoryEntity
import com.example.data.vision.DetectedUiElement
import com.example.data.vision.ScreenAnalysisResult
import com.example.model.AutoClickerProfile
import com.example.model.AutonomousAction
import com.example.model.BrowserAutonomousMode
import com.example.model.ClickRateMode
import com.example.model.ExtendedClickType
import com.example.model.KnowledgeEntry
import com.example.model.MacroSequence
import com.example.model.MacroStep
import com.example.model.MouseButtonType
import com.example.model.PointerStyle
import com.example.model.Profession
import com.example.model.QuantumMeetingRoomState
import com.example.model.ScientistAnalysis
import com.example.model.SmartVisualTriggerRule
import com.example.model.TargetPoint

enum class AutomationStatus {
    IDLE,
    RUNNING,
    PAUSED
}

enum class ActionType(val label: String, val description: String) {
    SINGLE_TAP("Toque Simples", "Toque rápido na posição alvo"),
    DOUBLE_TAP("Toque Duplo", "Dois toques consecutivos rápidos"),
    LONG_PRESS("Toque Longo", "Segura pressionado por 1 segundo"),
    SWIPE_UP("Deslizar p/ Cima", "Gesto de arrasto para cima")
}

enum class LogLevel {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

data class LogEntry(
    val id: Long,
    val timeFormatted: String,
    val message: String,
    val level: LogLevel = LogLevel.INFO
)

data class ExecutionStats(
    val totalSessions: Int = 0,
    val totalClicksAllTime: Long = 0L,
    val successfulExecutions: Long = 0L,
    val totalArbitrageProfitCentavos: Double = 0.0,
    val screensScannedCount: Int = 0
)

enum class AppTab(val label: String) {
    CONTROLS("Painel Turbo"),
    VISUAL_TRIGGERS("Gatilhos & Macros"),
    LIVE_CAMERA_VOICE("Modo Live 24/7 & Câmeras"),
    PROFILES("Perfis Microsoft"),
    OVERLAY("Sobreposição HUD"),
    QUANTUM_COUNCIL("Sala Quântica"),
    FOREX_ARBITRAGE("Câmbio"),
    SCREEN_VISION("Visão IA"),
    MOUSE_ARENA("Mouse"),
    BROWSER("Chrome"),
    BRAIN("Cérebro"),
    AI_SCIENTIST("Cientista")
}

data class MainUiState(
    val selectedTab: AppTab = AppTab.CONTROLS,
    val status: AutomationStatus = AutomationStatus.IDLE,
    val clickIntervalMs: Long = 500L,
    val repeatCount: Int = 50, // 0 = infinito
    val completedClicks: Int = 0,
    val targetX: Int = 540,
    val targetY: Int = 960,
    val actionType: ActionType = ActionType.SINGLE_TAP,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
    val overlayHelperEnabled: Boolean = true,
    val logs: List<LogEntry> = emptyList(),
    val isLoading: Boolean = false,
    val stats: ExecutionStats = ExecutionStats(),

    // Microsoft Store Cloned Features (9mszwlljjvb1 & 9n31lzkvgqvp)
    val rateMode: ClickRateMode = ClickRateMode.CPS,
    val targetCps: Int = 100, // 1 to 1000 CPS
    val intervalHours: Int = 0,
    val intervalMinutes: Int = 0,
    val intervalSeconds: Int = 0,
    val intervalMillis: Long = 10L,
    val mouseButton: MouseButtonType = MouseButtonType.LEFT,
    val extendedClickType: ExtendedClickType = ExtendedClickType.SINGLE,
    val holdDurationMs: Long = 45L,
    val coordinateJitterRadiusPx: Int = 4,
    val measuredLiveCps: Double = 0.0,

    // Unlimited Profiles Management
    val profiles: List<AutoClickerProfile> = MicrosoftProfilePresets.getDefaultProfiles(),
    val selectedProfile: AutoClickerProfile = MicrosoftProfilePresets.getDefaultProfiles().first(),

    // Multi-Point Numbered Targets Sequence
    val multiPoints: List<TargetPoint> = emptyList(),
    val activeMultiPointIndex: Int = 0,
    val multiPointModeActive: Boolean = false,

    // Smart Visual Triggers (Image & Color Recognition)
    val visualTriggerRules: List<SmartVisualTriggerRule> = MicrosoftProfilePresets.getDefaultVisualTriggers(),
    val isVisualTriggerScannerActive: Boolean = false,
    val lastVisualTriggerFired: String = "",

    // Keyboard & Mouse Macro Recorder & Sequencer
    val macros: List<MacroSequence> = MicrosoftProfilePresets.getDefaultMacros(),
    val selectedMacro: MacroSequence? = MicrosoftProfilePresets.getDefaultMacros().firstOrNull(),
    val isMacroRecording: Boolean = false,
    val recordedMacroSteps: List<MacroStep> = emptyList(),
    val isMacroPlaying: Boolean = false,
    val macroPlayingStep: Int = -1,

    // Live Mode: Cameras (Front/Rear) & 24/7 Voice Listener & Audible TTS
    val isLiveCameraActive: Boolean = false,
    val cameraFacingFront: Boolean = false, // false = Back, true = Front
    val isVoiceAssistantListening: Boolean = false,
    val isVoiceAudibleEnabled: Boolean = true,
    val lastVoiceCommandHeard: String = "",
    val lastVoiceSpokenResponse: String = "Assistente pronto para escutar.",
    val audioEnergyLevel: Float = 0f,
    val hasCameraPermission: Boolean = false,
    val hasAudioPermission: Boolean = false,

    // Visual Pointer & Cursor Settings
    val isMousePointerVisible: Boolean = true,
    val pointerStyle: PointerStyle = PointerStyle.MOUSE_ARROW,
    val pointerScale: Float = 1.0f,
    val lastClickPulseTime: Long = 0L,

    // Jitter & Anti-Detection Bio-variance
    val jitterEnabled: Boolean = true,
    val jitterRangeMs: Long = 15L,

    // System Overlay & Physical Accessibility Service
    val isOverlayServiceRunning: Boolean = false,
    val hasOverlayPermission: Boolean = false,
    val isAccessibilityServiceActive: Boolean = false,

    // Autonomous Neural Brain & Evolutive Professions
    val professions: List<Profession> = emptyList(),
    val wisdomEntries: List<KnowledgeEntry> = emptyList(),
    val isLearningOnline: Boolean = false,
    val newProfessionInput: String = "",
    val onlineSearchTopicInput: String = "otimização de reflexos e metagames",

    // Autonomous Google Chrome Browser Agent
    val browserUrl: String = "https://gemini.google.com",
    val isBrowserLoading: Boolean = false,
    val browserProgress: Float = 0f,
    val browserAutonomousMode: BrowserAutonomousMode = BrowserAutonomousMode.TALK_TO_GEMINI,
    val browserCustomPrompt: String = "Explique como treinar reflexos para jogos e sugira um prompt criativo para gerar imagem.",
    val isBrowserAutoPilotActive: Boolean = false,
    val browserConsoleOutput: String = "Pronto para navegar e auto-aprender.",

    // AI Scientist (OmniSciência & Game Solver)
    val isConsultingAi: Boolean = false,
    val aiUserQuery: String = "Otimizar velocidade e CPS para subir de nível rápido em jogos idle",
    val aiGameContext: String = "Geral / Jogos Clicker e RPG",
    val customApiKey: String = "",
    val scientistAnalysis: ScientistAnalysis? = null,
    val autonomousActions: List<AutonomousAction> = emptyList(),
    val isAutonomousRoutineActive: Boolean = false,
    val currentAutonomousStep: Int = 0,

    // --- Screen Vision & Room Memory Database ---
    val isScanningScreen: Boolean = false,
    val lastScreenAnalysis: ScreenAnalysisResult? = null,
    val selectedScreenElement: DetectedUiElement? = null,
    val screenMemoriesHistory: List<ScreenMemoryEntity> = emptyList(),
    val autonomousActionsHistory: List<AutonomousActionEntity> = emptyList(),

    // --- Real-time Forex & Arbitrage Currency Engine ---
    val currencyQuotes: Map<String, CurrencyQuote> = emptyMap(),
    val selectedCurrencyPair: String = "USD/BRL",
    val arbitrageOpportunities: List<ArbitrageOpportunity> = emptyList(),
    val arbitrageSignalsHistory: List<ArbitrageSignalEntity> = emptyList(),
    val isAutonomousPatrolRunning: Boolean = false,
    val simulatedTradeAmount: Double = 1000.0,
    val profitGoalCentavos: Double = 12.0,

    // --- Quantum Meeting Room & Collective Brain ---
    val quantumMeetingState: QuantumMeetingRoomState = QuantumMeetingRoomState()
)
