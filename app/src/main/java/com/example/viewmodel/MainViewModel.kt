package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.viewModelScope
import com.example.base.BaseAndroidViewModel
import com.example.data.AutonomousBrainEngine
import com.example.data.GeminiScientistService
import com.example.data.MicrosoftClickerManager
import com.example.data.MicrosoftProfilePresets
import com.example.data.QuantumCouncilEngine
import com.example.data.forex.ArbitrageOpportunity
import com.example.data.forex.ForexArbitrageEngine
import com.example.data.voice.VoiceCommandAction
import com.example.data.voice.VoiceLiveAssistantEngine
import com.example.data.local.AppDatabase
import com.example.data.local.AppRepository
import com.example.data.local.entity.ArbitrageSignalEntity
import com.example.data.local.entity.AutonomousActionEntity
import com.example.data.local.entity.CurrencyRateEntity
import com.example.data.local.entity.ScreenMemoryEntity
import com.example.data.vision.DetectedUiElement
import com.example.data.vision.ScreenVisionScanner
import com.example.model.AutoClickerProfile
import com.example.model.AutonomousAction
import com.example.model.BrowserAutonomousMode
import com.example.model.ClickRateMode
import com.example.model.ExtendedClickType
import com.example.model.KnowledgeEntry
import com.example.model.MacroActionType
import com.example.model.MacroSequence
import com.example.model.MacroStep
import com.example.model.MouseButtonType
import com.example.model.PointerStyle
import com.example.model.ProfileCategory
import com.example.model.SmartVisualTriggerRule
import com.example.model.SpecialistRole
import com.example.model.TargetPoint
import com.example.model.VisualTriggerAction
import com.example.model.VisualTriggerCondition
import com.example.service.AutoClickAccessibilityService
import com.example.service.FloatingOverlayService
import com.example.service.OverlayBridge
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Main ViewModel driving the IAut Clic engine.
 * Encapsulates automation cycles, AI Scientist consultations,
 * mouse pointer coordinates, Room persistence, Forex arbitrage engine,
 * and floating overlay synchronization.
 */
class MainViewModel(application: Application) :
    BaseAndroidViewModel<MainUiState, MainUiEffect>(application, MainUiState()) {

    private var automationJob: Job? = null
    private var autonomousRoutineJob: Job? = null
    private var autonomousPatrolJob: Job? = null
    private var forexTickerJob: Job? = null

    private val timeFormatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    private val scientistService = GeminiScientistService()
    private val brainEngine = AutonomousBrainEngine()
    private val forexEngine = ForexArbitrageEngine()
    private val quantumEngine = QuantumCouncilEngine()
    private val repository = AppRepository(AppDatabase.getDatabase(application))
    val msManager = MicrosoftClickerManager(viewModelScope)
    val voiceAssistantEngine = VoiceLiveAssistantEngine(application)

    private var quantumCouncilJob: Job? = null

    init {
        checkOverlayPermission()
        observeOverlayBridge()
        initAutonomousBrain()
        observeAccessibilityService()
        observeRoomDatabase()
        observeForexEngine()
        startForexTicker()
        observeQuantumCouncil()
        startQuantumCouncilLoop()
        observeMicrosoftManager()
        initVoiceAssistant()
        addLog("IAut Clic inicializado com Engine 1000 CPS, Comandos de Voz 24/7 e Parada de Emergência.", LogLevel.INFO)
    }

    private fun observeQuantumCouncil() {
        viewModelScope.launch {
            quantumEngine.meetingState.collectLatest { roomState ->
                updateState { copy(quantumMeetingState = roomState) }
            }
        }
    }

    private fun startQuantumCouncilLoop() {
        quantumCouncilJob?.cancel()
        quantumCouncilJob = viewModelScope.launch {
            while (isActive) {
                val newRecruit = quantumEngine.tickQuantumMeetingCycle()
                if (newRecruit != null) {
                    addLog(newRecruit, LogLevel.SUCCESS)
                    sendEffect(MainUiEffect.ShowSnackbar(newRecruit))
                }
                delay(3200L)
            }
        }
    }

    private fun initAutonomousBrain() {
        val defaultProfs = brainEngine.getDefaultProfessions()
        val defaultWisdom = brainEngine.getDefaultWisdom()
        updateState {
            copy(
                professions = defaultProfs,
                wisdomEntries = defaultWisdom
            )
        }
    }

    private fun observeRoomDatabase() {
        viewModelScope.launch {
            repository.screenMemories.collectLatest { memories ->
                updateState { copy(screenMemoriesHistory = memories) }
            }
        }
        viewModelScope.launch {
            repository.arbitrageSignals.collectLatest { signals ->
                updateState { copy(arbitrageSignalsHistory = signals) }
            }
        }
        viewModelScope.launch {
            repository.autonomousActions.collectLatest { actions ->
                updateState { copy(autonomousActionsHistory = actions) }
            }
        }
    }

    private fun observeForexEngine() {
        viewModelScope.launch {
            forexEngine.quotes.collectLatest { quotesMap ->
                updateState { copy(currencyQuotes = quotesMap) }
                val selected = quotesMap[currentState.selectedCurrencyPair]
                if (selected != null) {
                    val ticker = "${selected.pair} ${"%.4f".format(selected.ask)} | Spread: +${"%.1f".format(selected.spreadCentavos)}¢"
                    OverlayBridge.spreadTickerInfo.value = ticker
                }
            }
        }
        viewModelScope.launch {
            forexEngine.arbitrageOpportunities.collectLatest { opps ->
                updateState { copy(arbitrageOpportunities = opps) }
            }
        }
    }

    private fun startForexTicker() {
        forexTickerJob?.cancel()
        forexTickerJob = viewModelScope.launch {
            while (isActive) {
                forexEngine.tickMarket()
                delay(2500L)
            }
        }
    }

    private fun observeAccessibilityService() {
        viewModelScope.launch {
            AutoClickAccessibilityService.isServiceActive.collectLatest { active ->
                updateState { copy(isAccessibilityServiceActive = active) }
            }
        }
    }

    private fun observeOverlayBridge() {
        // Sync commands originating from the Floating Window overlay
        viewModelScope.launch {
            OverlayBridge.commands.collectLatest { command ->
                when (command) {
                    is OverlayBridge.OverlayCommand.StartAutomation -> startAutomation()
                    is OverlayBridge.OverlayCommand.PauseAutomation -> pauseAutomation()
                    is OverlayBridge.OverlayCommand.StopAutomation -> stopAutomation()
                    is OverlayBridge.OverlayCommand.PerformSingleClick -> performManualTest()
                    is OverlayBridge.OverlayCommand.UpdateTarget -> setTargetCoordinates(command.x, command.y)
                    is OverlayBridge.OverlayCommand.TriggerAiConsultation -> consultScientist()
                    is OverlayBridge.OverlayCommand.ScanScreen -> scanActiveScreen()
                    is OverlayBridge.OverlayCommand.ToggleAutonomousTrader -> toggleAutonomousPatrol()
                    is OverlayBridge.OverlayCommand.CloseOverlay -> stopOverlayService()
                    is OverlayBridge.OverlayCommand.ToggleVisualTriggerScanner -> toggleVisualTriggerScanner(!currentState.isVisualTriggerScannerActive)
                    is OverlayBridge.OverlayCommand.ToggleMacroRecording -> toggleMacroRecording()
                    is OverlayBridge.OverlayCommand.CycleProfileNext -> cycleNextProfile()
                    is OverlayBridge.OverlayCommand.AddMultiPoint -> addMultiPoint()
                    is OverlayBridge.OverlayCommand.RemoveLastMultiPoint -> removeLastMultiPoint()
                    is OverlayBridge.OverlayCommand.SetCpsRate -> setTargetCps(command.cps)
                    is OverlayBridge.OverlayCommand.SelectTargetPoint -> selectMultiPoint(command.pointIndex)
                    is OverlayBridge.OverlayCommand.EmergencyKillSwitch -> emergencyStopAll()
                    is OverlayBridge.OverlayCommand.ToggleVoiceListening -> {
                        if (currentState.isVoiceAssistantListening) stopVoiceListening() else startVoiceListening()
                    }
                    is OverlayBridge.OverlayCommand.SwitchCameraFacing -> switchCameraFacing()
                    is OverlayBridge.OverlayCommand.ScrollDown,
                    is OverlayBridge.OverlayCommand.ScrollUp,
                    is OverlayBridge.OverlayCommand.CenterPointer,
                    is OverlayBridge.OverlayCommand.TogglePointerVisibility,
                    is OverlayBridge.OverlayCommand.MovePointerSmoothly -> {
                        // Handled directly within FloatingOverlayService
                    }
                }
            }
        }

        // Monitor if overlay service is running
        viewModelScope.launch {
            OverlayBridge.isOverlayActive.collectLatest { active ->
                updateState { copy(isOverlayServiceRunning = active) }
            }
        }
    }

    private fun observeMicrosoftManager() {
        viewModelScope.launch {
            msManager.measuredCps.collectLatest { cps ->
                updateState { copy(measuredLiveCps = cps) }
            }
        }
        viewModelScope.launch {
            msManager.visualTriggerEngine.activeRules.collectLatest { rules ->
                updateState { copy(visualTriggerRules = rules) }
            }
        }
        viewModelScope.launch {
            msManager.macroEngine.macros.collectLatest { macroList ->
                updateState { copy(macros = macroList) }
            }
        }
        viewModelScope.launch {
            msManager.macroEngine.recordedSteps.collectLatest { steps ->
                updateState { copy(recordedMacroSteps = steps) }
            }
        }
        viewModelScope.launch {
            msManager.macroEngine.isRecording.collectLatest { rec ->
                updateState { copy(isMacroRecording = rec) }
                OverlayBridge.isMacroRecording.value = rec
            }
        }
        viewModelScope.launch {
            msManager.macroEngine.currentPlayingStep.collectLatest { step ->
                updateState { copy(macroPlayingStep = step, isMacroPlaying = step >= 0) }
            }
        }
    }

    fun setTab(tab: AppTab) {
        updateState { copy(selectedTab = tab) }
    }

    /**
     * Start or resume the automated clicking loop with 1000 CPS and Multi-Point capability.
     */
    fun startAutomation() {
        if (currentState.status == AutomationStatus.RUNNING) return
        AutoClickAccessibilityService.resumeGestures()

        val isResume = currentState.status == AutomationStatus.PAUSED
        updateState {
            copy(
                status = AutomationStatus.RUNNING,
                stats = stats.copy(
                    totalSessions = if (isResume) stats.totalSessions else stats.totalSessions + 1
                )
            )
        }
        OverlayBridge.automationStatus.value = AutomationStatus.RUNNING

        val speedDesc = if (currentState.rateMode == ClickRateMode.CPS) "${currentState.targetCps} CPS" else "${currentState.intervalMillis}ms"
        val actionMsg = if (isResume) "Automação retomada ($speedDesc)" else "Automação iniciada ($speedDesc)"
        addLog("$actionMsg [Modo: ${currentState.rateMode.displayName}, Alvo: (${currentState.targetX}, ${currentState.targetY})]", LogLevel.INFO)
        sendEffect(MainUiEffect.ShowSnackbar(actionMsg))

        automationJob?.cancel()
        automationJob = viewModelScope.launch {
            while (isActive && currentState.status == AutomationStatus.RUNNING) {
                if (currentState.repeatCount > 0 && currentState.completedClicks >= currentState.repeatCount) {
                    onAutomationCompleted()
                    break
                }

                // If Multi-Point Sequence mode is active
                if (currentState.multiPointModeActive && currentState.multiPoints.isNotEmpty()) {
                    val points = currentState.multiPoints
                    for ((idx, point) in points.withIndex()) {
                        if (!isActive || currentState.status != AutomationStatus.RUNNING) break
                        updateState { copy(activeMultiPointIndex = idx, targetX = point.x, targetY = point.y) }
                        OverlayBridge.currentX.value = point.x
                        OverlayBridge.currentY.value = point.y
                        OverlayBridge.activeTargetPointIndex.value = idx

                        for (c in 1..point.clickCount) {
                            if (!isActive || currentState.status != AutomationStatus.RUNNING) break
                            val (jitterX, jitterY) = msManager.computeJitteredCoordinates(
                                point.x, point.y, currentState.coordinateJitterRadiusPx
                            )
                            executeSingleClickAt(jitterX, jitterY)
                            delay(point.delayAfterMs.coerceAtLeast(10L))
                        }
                    }
                } else {
                    // Standard Single Target Mode (supports 1000 CPS!)
                    val baseInterval = msManager.calculateTargetIntervalMs(currentState)
                    var effectiveDelay = baseInterval
                    if (currentState.jitterEnabled) {
                        val jitterDelta = Random.nextLong(-currentState.jitterRangeMs, currentState.jitterRangeMs + 1)
                        effectiveDelay = (effectiveDelay + jitterDelta).coerceAtLeast(1L)
                    }

                    if (currentState.rateMode == ClickRateMode.CPS && currentState.targetCps >= 200) {
                        val (jitterX, jitterY) = msManager.computeJitteredCoordinates(
                            currentState.targetX, currentState.targetY, currentState.coordinateJitterRadiusPx
                        )
                        val burstCount = (currentState.targetCps / 25).coerceIn(4, 40)
                        val delivered = msManager.executeUltraSpeedBurst(
                            jitterX, jitterY, burstCount, currentState.extendedClickType, currentState.mouseButton
                        )
                        recordClicksBatch(delivered)
                        triggerClickFeedback()
                        delay(25L)
                    } else {
                        delay(effectiveDelay)
                        val (jitterX, jitterY) = msManager.computeJitteredCoordinates(
                            currentState.targetX, currentState.targetY, currentState.coordinateJitterRadiusPx
                        )
                        executeSingleClickAt(jitterX, jitterY)
                    }
                }
            }
        }
    }

    /**
     * Pause the running automation.
     */
    fun pauseAutomation() {
        if (currentState.status != AutomationStatus.RUNNING) return

        automationJob?.cancel()
        updateState { copy(status = AutomationStatus.PAUSED) }
        OverlayBridge.automationStatus.value = AutomationStatus.PAUSED
        addLog("Automação pausada no clique #${currentState.completedClicks}", LogLevel.WARNING)
        sendEffect(MainUiEffect.ShowSnackbar("Automação pausada"))
    }

    /**
     * Stop and reset automation.
     */
    fun stopAutomation() {
        automationJob?.cancel()
        autonomousRoutineJob?.cancel()
        AutoClickAccessibilityService.clearAllGestures()
        updateState {
            copy(
                status = AutomationStatus.IDLE,
                completedClicks = 0,
                isAutonomousRoutineActive = false
            )
        }
        OverlayBridge.automationStatus.value = AutomationStatus.IDLE
        OverlayBridge.clickCounter.value = 0
        addLog("Automação finalizada. Contador zerado e gestos limpos.", LogLevel.INFO)
        sendEffect(MainUiEffect.ShowSnackbar("Automação finalizada"))
    }

    /**
     * Update target coordinates (X, Y) for mouse cursor and click.
     */
    fun setTargetCoordinates(x: Int, y: Int) {
        val clampedX = x.coerceAtLeast(0)
        val clampedY = y.coerceAtLeast(0)
        updateState { copy(targetX = clampedX, targetY = clampedY) }
        OverlayBridge.currentX.value = clampedX
        OverlayBridge.currentY.value = clampedY
    }

    fun setPointerVisible(visible: Boolean) {
        updateState { copy(isMousePointerVisible = visible) }
        OverlayBridge.pointerVisible.value = visible
    }

    fun setPointerStyle(style: PointerStyle) {
        updateState { copy(pointerStyle = style) }
        OverlayBridge.pointerStyle.value = style
    }

    fun setIntervalMs(intervalMs: Long) {
        val coerced = intervalMs.coerceIn(30L, 10000L)
        updateState { copy(clickIntervalMs = coerced) }
        OverlayBridge.clickInterval.value = coerced
    }

    fun setRepeatCount(count: Int) {
        val coerced = count.coerceIn(0, 100000)
        updateState { copy(repeatCount = coerced) }
        OverlayBridge.targetRepeat.value = coerced
    }

    fun setActionType(actionType: ActionType) {
        updateState { copy(actionType = actionType) }
        addLog("Tipo de ação alterado para ${actionType.label}", LogLevel.INFO)
    }

    fun toggleJitter(enabled: Boolean) {
        updateState { copy(jitterEnabled = enabled) }
        addLog("Jitter biométrico anti-detecção: ${if (enabled) "Ativado" else "Desativado"}", LogLevel.INFO)
    }

    fun setJitterRange(rangeMs: Long) {
        updateState { copy(jitterRangeMs = rangeMs.coerceIn(5L, 300L)) }
    }

    fun toggleVibration(enabled: Boolean) {
        updateState { copy(vibrationEnabled = enabled) }
    }

    fun toggleSound(enabled: Boolean) {
        updateState { copy(soundEnabled = enabled) }
    }

    /**
     * Execute a single test click at the current target.
     */
    fun performManualTest() {
        viewModelScope.launch {
            addLog("Disparo manual: ${currentState.actionType.label} em (${currentState.targetX}, ${currentState.targetY})", LogLevel.SUCCESS)
            triggerClickFeedback()
            updateState {
                copy(
                    stats = stats.copy(
                        totalClicksAllTime = stats.totalClicksAllTime + 1,
                        successfulExecutions = stats.successfulExecutions + 1
                    )
                )
            }
        }
    }

    /**
     * Check overlay permission status.
     */
    fun checkOverlayPermission() {
        val hasPermission = Settings.canDrawOverlays(getApplication())
        updateState { copy(hasOverlayPermission = hasPermission) }
    }

    /**
     * Toggle Floating Screen Overlay service.
     */
    fun toggleOverlayService() {
        val app = getApplication<Application>()
        if (!Settings.canDrawOverlays(app)) {
            sendEffect(MainUiEffect.RequestOverlayPermission)
            sendEffect(MainUiEffect.ShowToast("Conceda a permissão de sobreposição para ativar o modo flutuante"))
            return
        }

        if (currentState.isOverlayServiceRunning) {
            stopOverlayService()
        } else {
            startOverlayService()
        }
    }

    fun startOverlayService() {
        val app = getApplication<Application>()
        val intent = Intent(app, FloatingOverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            app.startForegroundService(intent)
        } else {
            app.startService(intent)
        }
        updateState { copy(isOverlayServiceRunning = true) }
        addLog("Serviço de sobreposição e mouse flutuante ativado sobre a tela.", LogLevel.SUCCESS)
        sendEffect(MainUiEffect.ShowSnackbar("Sobreposição e mouse flutuante ativos"))
    }

    fun stopOverlayService() {
        val app = getApplication<Application>()
        val intent = Intent(app, FloatingOverlayService::class.java)
        app.stopService(intent)
        updateState { copy(isOverlayServiceRunning = false) }
        addLog("Serviço de sobreposição encerrado.", LogLevel.INFO)
    }

    // --- AI Scientist & Autonomous Game Solver Engine ---

    fun setAiUserQuery(query: String) {
        updateState { copy(aiUserQuery = query) }
    }

    fun setAiGameContext(context: String) {
        updateState { copy(aiGameContext = context) }
    }

    fun setCustomApiKey(key: String) {
        updateState { copy(customApiKey = key) }
    }

    /**
     * Consult the Gemini AI Universal Scientist.
     */
    fun consultScientist() {
        if (currentState.isConsultingAi) return

        updateState { copy(isConsultingAi = true) }
        addLog("Consultando o Cientista Universal com busca online...", LogLevel.INFO)

        viewModelScope.launch {
            val result = scientistService.consultScientist(
                userQuery = currentState.aiUserQuery,
                gameContext = currentState.aiGameContext,
                customApiKey = currentState.customApiKey.takeIf { it.isNotBlank() }
            )

            result.onSuccess { analysis ->
                updateState {
                    copy(
                        isConsultingAi = false,
                        scientistAnalysis = analysis,
                        autonomousActions = analysis.autonomousPlan
                    )
                }
                addLog("Cientista Universal: Análise concluída. Intervalo ideal: ${analysis.recommendedIntervalMs}ms, CPS: ${String.format("%.1f", analysis.theoreticalCps)}", LogLevel.SUCCESS)
                sendEffect(MainUiEffect.ShowSnackbar("Cálculos científicos prontos!"))
            }.onFailure { error ->
                updateState { copy(isConsultingAi = false) }
                addLog("Erro na consulta do cientista: ${error.message}", LogLevel.ERROR)
                sendEffect(MainUiEffect.ShowToast("Falha na consulta: ${error.message}"))
            }
        }
    }

    /**
     * Apply the AI Scientist's mathematically computed parameters directly to the autoclicker.
     */
    fun applyScientistParameters() {
        val analysis = currentState.scientistAnalysis ?: return
        setIntervalMs(analysis.recommendedIntervalMs)
        setJitterRange(analysis.recommendedJitterMs)
        updateState { copy(jitterEnabled = true) }
        addLog("Parâmetros da IA aplicados: ${analysis.recommendedIntervalMs}ms (Jitter: ±${analysis.recommendedJitterMs}ms)", LogLevel.SUCCESS)
        sendEffect(MainUiEffect.ShowToast("Parâmetros matemáticos aplicados com sucesso!"))
    }

    /**
     * Execute autonomous multi-step routine.
     */
    fun startAutonomousRoutine() {
        val plan = currentState.autonomousActions
        if (plan.isEmpty()) {
            sendEffect(MainUiEffect.ShowToast("Nenhum plano autônomo carregado. Consulte o Cientista."))
            return
        }

        updateState {
            copy(
                isAutonomousRoutineActive = true,
                currentAutonomousStep = 0,
                status = AutomationStatus.RUNNING
            )
        }
        OverlayBridge.automationStatus.value = AutomationStatus.RUNNING

        autonomousRoutineJob?.cancel()
        autonomousRoutineJob = viewModelScope.launch {
            for ((index, action) in plan.withIndex()) {
                if (!isActive || !currentState.isAutonomousRoutineActive) break

                updateState { copy(currentAutonomousStep = index) }
                setTargetCoordinates(action.targetX, action.targetY)
                setIntervalMs(action.intervalMs)
                setActionType(action.actionType)
                addLog("Passo Autônomo ${index + 1}/${plan.size}: ${action.name} em (${action.targetX}, ${action.targetY})", LogLevel.INFO)

                // Execute clicks for this step
                val clicks = if (action.clicks > 0) action.clicks else 1
                for (c in 1..clicks) {
                    if (!isActive || !currentState.isAutonomousRoutineActive) break
                    delay(action.intervalMs)
                    executeActionTick()
                }

                if (action.delayAfterMs > 0) {
                    delay(action.delayAfterMs)
                }
            }

            updateState {
                copy(
                    isAutonomousRoutineActive = false,
                    status = AutomationStatus.IDLE
                )
            }
            OverlayBridge.automationStatus.value = AutomationStatus.IDLE
            addLog("Plano de jogo autônomo concluído com sucesso!", LogLevel.SUCCESS)
            sendEffect(MainUiEffect.ShowSnackbar("Jogo autônomo concluído!"))
        }
    }

    fun clearLogs() {
        updateState { copy(logs = emptyList()) }
        sendEffect(MainUiEffect.ShowToast("Histórico limpo"))
    }

    private fun executeActionTick() {
        val (jitterX, jitterY) = msManager.computeJitteredCoordinates(
            currentState.targetX, currentState.targetY, currentState.coordinateJitterRadiusPx
        )
        executeSingleClickAt(jitterX, jitterY)
    }

    private fun executeSingleClickAt(x: Int, y: Int) {
        val nextCount = currentState.completedClicks + 1
        updateState {
            copy(
                completedClicks = nextCount,
                stats = stats.copy(
                    totalClicksAllTime = stats.totalClicksAllTime + 1,
                    successfulExecutions = stats.successfulExecutions + 1
                )
            )
        }
        OverlayBridge.clickCounter.value = nextCount

        val physicalSuccess = msManager.executeClickGesture(
            x, y, currentState.extendedClickType, currentState.mouseButton, currentState.holdDurationMs
        )
        triggerClickFeedback()
        awardXpToProfession("prof_gamer", 1)

        if (nextCount <= 3 || nextCount % 25 == 0 || nextCount == currentState.repeatCount) {
            val modeMsg = if (physicalSuccess) "Toque real (Acessibilidade)" else "Simulação visual"
            addLog("Clique #${nextCount} [$modeMsg] [${currentState.mouseButton.displayName}] em ($x, $y)", LogLevel.SUCCESS)
        }
    }

    private fun recordClicksBatch(count: Int) {
        val nextCount = currentState.completedClicks + count
        updateState {
            copy(
                completedClicks = nextCount,
                stats = stats.copy(
                    totalClicksAllTime = stats.totalClicksAllTime + count,
                    successfulExecutions = stats.successfulExecutions + count
                )
            )
        }
        OverlayBridge.clickCounter.value = nextCount
        awardXpToProfession("prof_gamer", count)
        if (nextCount % 100 < count) {
            addLog("⚡ Rajada Turbo: #${nextCount} cliques a ${currentState.targetCps} CPS!", LogLevel.SUCCESS)
        }
    }

    // --- Microsoft Store Cloned Features Logic ---

    fun setClickRateMode(mode: ClickRateMode) {
        updateState { copy(rateMode = mode) }
        addLog("Modo de taxa alterado para: ${mode.displayName}", LogLevel.INFO)
    }

    fun setTargetCps(cps: Int) {
        val clamped = cps.coerceIn(1, 1000)
        updateState { copy(targetCps = clamped) }
        OverlayBridge.currentCps.value = clamped
        addLog("Taxa definida para $clamped CPS (Cliques/seg)", LogLevel.INFO)
    }

    fun setExactTime(hours: Int, minutes: Int, seconds: Int, millis: Long) {
        val safeH = hours.coerceIn(0, 99)
        val safeM = minutes.coerceIn(0, 59)
        val safeS = seconds.coerceIn(0, 59)
        val safeMs = millis.coerceIn(1L, 9999L)
        updateState {
            copy(
                intervalHours = safeH,
                intervalMinutes = safeM,
                intervalSeconds = safeS,
                intervalMillis = safeMs
            )
        }
        addLog("Intervalo exato: ${safeH}h ${safeM}m ${safeS}s ${safeMs}ms", LogLevel.INFO)
    }

    fun setMouseButton(button: MouseButtonType) {
        updateState { copy(mouseButton = button) }
        addLog("Botão do mouse: ${button.displayName}", LogLevel.INFO)
    }

    fun setExtendedClickType(type: ExtendedClickType) {
        updateState { copy(extendedClickType = type) }
        addLog("Tipo de clique: ${type.displayName}", LogLevel.INFO)
    }

    fun setCoordinateJitterRadius(radiusPx: Int) {
        val clamped = radiusPx.coerceIn(0, 30)
        updateState { copy(coordinateJitterRadiusPx = clamped) }
    }

    fun setHoldDuration(durationMs: Long) {
        updateState { copy(holdDurationMs = durationMs.coerceIn(10L, 3000L)) }
    }

    // Profiles Management
    fun selectProfile(profile: AutoClickerProfile) {
        updateState {
            copy(
                selectedProfile = profile,
                rateMode = profile.rateMode,
                targetCps = profile.targetCps,
                intervalHours = profile.intervalHours,
                intervalMinutes = profile.intervalMinutes,
                intervalSeconds = profile.intervalSeconds,
                intervalMillis = profile.intervalMillis,
                extendedClickType = profile.extendedClickType,
                mouseButton = profile.mouseButton,
                jitterEnabled = profile.jitterEnabled,
                jitterRangeMs = profile.jitterMs,
                coordinateJitterRadiusPx = profile.coordinateJitterRadiusPx,
                multiPoints = profile.multiPoints,
                multiPointModeActive = profile.multiPoints.isNotEmpty()
            )
        }
        OverlayBridge.currentCps.value = profile.targetCps
        OverlayBridge.activeProfileTitle.value = profile.title
        OverlayBridge.multiPointsList.value = profile.multiPoints
        addLog("Perfil ativado: ${profile.title}", LogLevel.SUCCESS)
        sendEffect(MainUiEffect.ShowSnackbar("Perfil '${profile.title}' ativado!"))
    }

    fun cycleNextProfile() {
        val all = currentState.profiles
        if (all.isEmpty()) return
        val currentIdx = all.indexOfFirst { it.id == currentState.selectedProfile.id }
        val nextIdx = (currentIdx + 1) % all.size
        selectProfile(all[nextIdx])
    }

    fun createNewProfile(title: String, category: ProfileCategory) {
        val trimmed = title.trim()
        if (trimmed.isBlank()) return
        val newProfile = AutoClickerProfile(
            id = "profile_${System.currentTimeMillis()}",
            title = trimmed,
            description = "Perfil personalizado com ${currentState.targetCps} CPS e jitter de ${currentState.coordinateJitterRadiusPx}px.",
            category = category,
            iconEmoji = "⚙️",
            rateMode = currentState.rateMode,
            targetCps = currentState.targetCps,
            intervalMillis = currentState.intervalMillis,
            extendedClickType = currentState.extendedClickType,
            mouseButton = currentState.mouseButton,
            jitterEnabled = currentState.jitterEnabled,
            jitterMs = currentState.jitterRangeMs,
            coordinateJitterRadiusPx = currentState.coordinateJitterRadiusPx,
            multiPoints = currentState.multiPoints,
            isBuiltIn = false
        )
        updateState { copy(profiles = profiles + newProfile) }
        selectProfile(newProfile)
        sendEffect(MainUiEffect.ShowToast("Perfil '$trimmed' salvo com sucesso!"))
    }

    fun deleteProfile(profileId: String) {
        val updated = currentState.profiles.filter { it.id != profileId }
        updateState { copy(profiles = updated) }
        if (currentState.selectedProfile.id == profileId) {
            updated.firstOrNull()?.let { selectProfile(it) }
        }
    }

    // Multi-Point Sequence Management
    fun addMultiPoint(x: Int = currentState.targetX, y: Int = currentState.targetY) {
        val nextId = currentState.multiPoints.size + 1
        val colors = listOf("#00E5FF", "#A855F7", "#10B981", "#F59E0B", "#EF4444")
        val color = colors[(nextId - 1) % colors.size]
        val newPoint = TargetPoint(
            id = nextId,
            x = x,
            y = y,
            delayAfterMs = 200L,
            clickCount = 1,
            label = "Ponto $nextId",
            colorHex = color
        )
        val updated = currentState.multiPoints + newPoint
        updateState { copy(multiPoints = updated, multiPointModeActive = true) }
        OverlayBridge.multiPointsList.value = updated
        addLog("Ponto #$nextId adicionado em ($x, $y)", LogLevel.INFO)
        sendEffect(MainUiEffect.ShowToast("Ponto #$nextId adicionado"))
    }

    fun removeLastMultiPoint() {
        if (currentState.multiPoints.isEmpty()) return
        val updated = currentState.multiPoints.dropLast(1)
        updateState {
            copy(
                multiPoints = updated,
                multiPointModeActive = updated.isNotEmpty()
            )
        }
        OverlayBridge.multiPointsList.value = updated
        sendEffect(MainUiEffect.ShowToast("Último ponto removido"))
    }

    fun selectMultiPoint(pointIndex: Int) {
        val points = currentState.multiPoints
        if (pointIndex in points.indices) {
            val p = points[pointIndex]
            updateState { copy(activeMultiPointIndex = pointIndex, targetX = p.x, targetY = p.y) }
            OverlayBridge.currentX.value = p.x
            OverlayBridge.currentY.value = p.y
        }
    }

    fun toggleMultiPointMode(enabled: Boolean) {
        updateState { copy(multiPointModeActive = enabled) }
        addLog("Modo Multi-Ponto: ${if (enabled) "Ativado" else "Desativado"}", LogLevel.INFO)
    }

    // Smart Visual Triggers Management
    fun toggleVisualTriggerScanner(active: Boolean = !currentState.isVisualTriggerScannerActive) {
        updateState { copy(isVisualTriggerScannerActive = active) }
        OverlayBridge.isVisualTriggerActive.value = active
        if (active) {
            msManager.startVisualTriggerScanner { rule, x, y ->
                onVisualTriggerFired(rule, x, y)
            }
            addLog("Scanner de Gatilhos Visuais ATIVADO.", LogLevel.SUCCESS)
        } else {
            msManager.stopVisualTriggerScanner()
            addLog("Scanner de Gatilhos Visuais PAUSADO.", LogLevel.INFO)
        }
    }

    fun toggleVisualTriggerRule(ruleId: String, enabled: Boolean) {
        msManager.visualTriggerEngine.toggleRule(ruleId, enabled)
    }

    fun addVisualTriggerRule(rule: SmartVisualTriggerRule) {
        msManager.visualTriggerEngine.addRule(rule)
        addLog("Novo Gatilho Visual adicionado: ${rule.name}", LogLevel.SUCCESS)
    }

    private fun onVisualTriggerFired(rule: SmartVisualTriggerRule, x: Int, y: Int) {
        val msg = "Gatilho Visual '${rule.name}' detectado em ($x, $y) -> ${rule.action.displayName}"
        addLog(msg, LogLevel.SUCCESS)
        updateState { copy(lastVisualTriggerFired = msg) }
        OverlayBridge.detectedTargetLabel.value = rule.name

        when (rule.action) {
            VisualTriggerAction.BURST_1000_CPS -> {
                val delivered = msManager.executeUltraSpeedBurst(x, y, burstCount = 12)
                recordClicksBatch(delivered)
                triggerClickFeedback()
                addLog("⚡ Rajada Turbo 1000 CPS disparada pelo Gatilho Visual ($delivered toques)!", LogLevel.SUCCESS)
            }
            VisualTriggerAction.CLICK_AT_TRIGGER_POINT -> {
                executeSingleClickAt(x, y)
            }
            VisualTriggerAction.EXECUTE_MACRO -> {
                val macro = currentState.selectedMacro ?: currentState.macros.firstOrNull()
                if (macro != null) {
                    playMacro(macro)
                }
            }
            VisualTriggerAction.NOTIFY_QUANTUM_COUNCIL -> {
                sendEffect(MainUiEffect.ShowSnackbar("Alerta Visual transmitido à Sala Quântica!"))
            }
            VisualTriggerAction.PAUSE_AUTOMATION -> {
                pauseAutomation()
            }
        }
    }

    // Keyboard & Mouse Macro Recorder & Playback
    fun toggleMacroRecording() {
        if (currentState.isMacroRecording) {
            stopMacroRecordingAndSave("Macro_${System.currentTimeMillis() % 10000}")
        } else {
            startMacroRecording()
        }
    }

    fun startMacroRecording() {
        msManager.macroEngine.startRecording()
        updateState { copy(isMacroRecording = true) }
        OverlayBridge.isMacroRecording.value = true
        addLog("Gravação de Macro iniciada. Dê toques na tela para registrar passos.", LogLevel.WARNING)
        sendEffect(MainUiEffect.ShowToast("Gravando macro..."))
    }

    fun recordMacroStepAt(x: Int, y: Int, type: MacroActionType = MacroActionType.CLICK) {
        msManager.macroEngine.recordClickStep(x, y, type)
    }

    fun stopMacroRecordingAndSave(name: String) {
        val saved = msManager.macroEngine.stopRecordingAndSave(name)
        updateState { copy(isMacroRecording = false) }
        OverlayBridge.isMacroRecording.value = false
        if (saved != null) {
            addLog("Macro '${saved.name}' gravado com ${saved.steps.size} passos!", LogLevel.SUCCESS)
            sendEffect(MainUiEffect.ShowSnackbar("Macro '${saved.name}' salvo!"))
        } else {
            addLog("Gravação de macro finalizada sem passos registrados.", LogLevel.INFO)
        }
    }

    fun selectMacro(macro: MacroSequence) {
        updateState { copy(selectedMacro = macro) }
        msManager.macroEngine.selectMacro(macro)
    }

    fun playMacro(macro: MacroSequence = currentState.selectedMacro ?: currentState.macros.first()) {
        updateState { copy(isMacroPlaying = true) }
        addLog("Executando macro '${macro.name}' (${macro.steps.size} passos)...", LogLevel.INFO)
        msManager.playMacroSequence(
            macro = macro,
            onStepUpdated = { stepIdx ->
                updateState { copy(macroPlayingStep = stepIdx) }
            },
            onCompleted = {
                updateState { copy(isMacroPlaying = false, macroPlayingStep = -1) }
                addLog("Macro '${macro.name}' concluído com sucesso!", LogLevel.SUCCESS)
            }
        )
    }

    fun stopMacro() {
        msManager.stopMacroPlayback()
        updateState { copy(isMacroPlaying = false, macroPlayingStep = -1) }
        addLog("Execução de macro interrompida.", LogLevel.WARNING)
    }

    // --- Autonomous Brain & Evolutive Professions ---

    fun addNewProfession(appNameOrTask: String) {
        val trimmed = appNameOrTask.trim()
        if (trimmed.isBlank()) return

        val newProf = brainEngine.createProfessionForApp(trimmed)
        updateState {
            copy(
                professions = professions + newProf,
                newProfessionInput = ""
            )
        }
        addLog("Nova profissão assimilada: ${newProf.title} (${newProf.iconEmoji}) com 3 habilidades operacionais!", LogLevel.SUCCESS)
        sendEffect(MainUiEffect.ShowToast("Profissão '${newProf.title}' criada com sucesso!"))
    }

    fun learnOnlineFromWeb(queryTopic: String) {
        val topic = queryTopic.trim()
        if (topic.isBlank() || currentState.isLearningOnline) return

        updateState { copy(isLearningOnline = true) }
        addLog("Cérebro pesquisando online na web sobre: '$topic'...", LogLevel.INFO)

        viewModelScope.launch {
            val result = brainEngine.learnOnlineFromWeb(topic)
            val entry = result.getOrNull()
            if (entry != null) {
                updateState {
                    copy(
                        isLearningOnline = false,
                        wisdomEntries = listOf(entry) + wisdomEntries
                    )
                }
                awardXpToProfession("prof_browser", entry.xpAwarded)
                awardXpToProfession("prof_scientist", (entry.xpAwarded * 0.5).toInt())
                addLog("Conhecimento assimilado: ${entry.topic} (+${entry.xpAwarded} XP)", LogLevel.SUCCESS)
                sendEffect(MainUiEffect.ShowSnackbar("Novo insight adicionado ao Banco de Sabedoria!"))
            } else {
                updateState { copy(isLearningOnline = false) }
                addLog("Tentativa de assimilação finalizada.", LogLevel.WARNING)
            }
        }
    }

    fun awardXpToProfession(profId: String, xp: Int) {
        updateState {
            val updated = professions.map { p ->
                if (p.id == profId || (profId == "prof_gamer" && p.category.contains("Jogos"))) {
                    val newXp = p.currentXp + xp
                    if (newXp >= p.requiredXp) {
                        val newLevel = p.level + 1
                        val nextReq = p.requiredXp + (newLevel * 150)
                        val updatedSkills = p.skills.mapIndexed { idx, s ->
                            if (!s.unlocked && idx <= newLevel) s.copy(unlocked = true) else s
                        }
                        addLog("🎉 LEVEL UP: ${p.title} alcançou o Nível $newLevel!", LogLevel.SUCCESS)
                        p.copy(
                            level = newLevel,
                            currentXp = newXp - p.requiredXp,
                            requiredXp = nextReq,
                            skills = updatedSkills
                        )
                    } else {
                        p.copy(currentXp = newXp)
                    }
                } else p
            }
            copy(professions = updated)
        }
    }

    fun setNewProfessionInput(text: String) {
        updateState { copy(newProfessionInput = text) }
    }

    fun setOnlineSearchTopicInput(text: String) {
        updateState { copy(onlineSearchTopicInput = text) }
    }

    // --- Autonomous Google Chrome Browser Agent ---

    fun setBrowserUrl(url: String) {
        updateState { copy(browserUrl = url) }
    }

    fun setBrowserLoading(isLoading: Boolean, progress: Float) {
        updateState { copy(isBrowserLoading = isLoading, browserProgress = progress) }
    }

    fun setBrowserAutonomousMode(mode: BrowserAutonomousMode) {
        updateState { copy(browserAutonomousMode = mode) }
    }

    fun setBrowserCustomPrompt(prompt: String) {
        updateState { copy(browserCustomPrompt = prompt) }
    }

    fun runAutonomousBrowserPilot() {
        val script = brainEngine.buildJavaScriptForAutonomousAction(
            currentState.browserAutonomousMode,
            currentState.browserCustomPrompt
        )
        updateState {
            copy(
                isBrowserAutoPilotActive = true,
                browserConsoleOutput = "Injetando rotina de automação na página..."
            )
        }
        sendEffect(MainUiEffect.ExecuteBrowserJs(script))
        awardXpToProfession("prof_gemini_agent", 40)
        addLog("Piloto autônomo executando [${currentState.browserAutonomousMode.label}] na página web.", LogLevel.INFO)
    }

    fun onBrowserJsResult(result: String) {
        val cleanResult = result.removeSurrounding("\"").replace("\\\"", "\"")
        updateState {
            copy(
                isBrowserAutoPilotActive = false,
                browserConsoleOutput = cleanResult
            )
        }
        addLog("Resposta da página web: $cleanResult", LogLevel.SUCCESS)

        if (cleanResult.contains("title") || cleanResult.contains("content")) {
            val entry = KnowledgeEntry(
                id = java.util.UUID.randomUUID().toString(),
                sourceUrlOrApp = currentState.browserUrl,
                topic = "Extração Web Autônoma",
                insight = cleanResult.take(300),
                timestamp = System.currentTimeMillis(),
                professionName = "Interator Gemini & IA Web",
                xpAwarded = 65
            )
            updateState { copy(wisdomEntries = listOf(entry) + wisdomEntries) }
            awardXpToProfession("prof_gemini_agent", 50)
            sendEffect(MainUiEffect.ShowSnackbar("Novo conhecimento extraído do navegador!"))
        }
    }

    fun openAccessibilitySettings() {
        sendEffect(MainUiEffect.OpenAccessibilitySettings)
    }

    private fun triggerClickFeedback() {
        val now = System.currentTimeMillis()
        updateState { copy(lastClickPulseTime = now) }
        OverlayBridge.emitClickPulse()
        sendEffect(MainUiEffect.ClickImpactPulse(currentState.targetX, currentState.targetY))

        if (currentState.vibrationEnabled) {
            sendEffect(MainUiEffect.TriggerHaptic)
        }
    }

    private fun onAutomationCompleted() {
        updateState { copy(status = AutomationStatus.IDLE) }
        OverlayBridge.automationStatus.value = AutomationStatus.IDLE
        addLog("Ciclo finalizado: ${currentState.repeatCount} ações executadas.", LogLevel.SUCCESS)
        sendEffect(MainUiEffect.ShowSnackbar("Meta de cliques atingida!"))
    }

    private fun addLog(message: String, level: LogLevel) {
        val entry = LogEntry(
            id = System.currentTimeMillis() + (0..999).random(),
            timeFormatted = timeFormatter.format(Date()),
            message = message,
            level = level
        )
        updateState {
            val updatedLogs = (listOf(entry) + logs).take(150)
            copy(logs = updatedLogs)
        }
        sendEffect(MainUiEffect.ScrollToLatestLog)
    }

    // =========================================================================
    // SCREEN VISION, ROOM MEMORY & AUTONOMOUS MOUSE NAVIGATION
    // =========================================================================

    /**
     * Scans what is currently rendered on the active window using AccessibilityNodeInfo.
     * Extracts text, prices, currency codes, actionable buttons, and saves memory into Room.
     */
    fun scanActiveScreen() {
        if (!currentState.isAccessibilityServiceActive) {
            sendEffect(MainUiEffect.ShowSnackbar("Ative o Serviço de Acessibilidade para permitir a leitura da tela!"))
            addLog("Tentativa de leitura de tela sem Acessibilidade ativa.", LogLevel.WARNING)
            return
        }

        updateState { copy(isScanningScreen = true) }
        OverlayBridge.aiStatusMessage.value = "👁️ Lendo tela..."

        viewModelScope.launch {
            try {
                val result = ScreenVisionScanner.scanActiveWindow()
                if (result != null) {
                    updateState {
                        copy(
                            lastScreenAnalysis = result,
                            isScanningScreen = false,
                            stats = stats.copy(screensScannedCount = stats.screensScannedCount + 1)
                        )
                    }

                    // Save structured screen capture into Room Database
                    val entity = ScreenMemoryEntity(
                        packageName = result.packageName,
                        windowTitle = result.windowTitle,
                        extractedSummary = "Detectados ${result.totalElementsCount} nós (${result.actionableTargets.size} acionáveis)",
                        detectedCurrencies = result.detectedCurrencies.joinToString(", "),
                        detectedRates = result.detectedRatesAndValues.take(5).joinToString(" | "),
                        clickableElementsCount = result.actionableTargets.size
                    )
                    repository.saveScreenMemory(entity)

                    val bestTarget = result.bestActionTarget
                    if (bestTarget != null) {
                        val targetLabel = "${bestTarget.text} ${bestTarget.contentDescription}".trim()
                        OverlayBridge.detectedTargetLabel.value = targetLabel
                        OverlayBridge.aiStatusMessage.value = "Alvo: ${targetLabel.take(18)}"
                        addLog("Tela lida: ${result.packageName} - ${result.detectedCurrencies.size} moedas detectadas. Alvo sugerido: '$targetLabel'", LogLevel.SUCCESS)
                    } else {
                        OverlayBridge.aiStatusMessage.value = "Tela mapeada (${result.totalElementsCount} nós)"
                        addLog("Tela lida: ${result.packageName} (${result.totalElementsCount} nós mapeados).", LogLevel.INFO)
                    }
                    sendEffect(MainUiEffect.ShowSnackbar("Tela lida com sucesso!"))
                } else {
                    updateState { copy(isScanningScreen = false) }
                    OverlayBridge.aiStatusMessage.value = "Sem nós acessíveis"
                    addLog("Nenhum nó de janela ativa pôde ser lido.", LogLevel.WARNING)
                    sendEffect(MainUiEffect.ShowSnackbar("Nenhuma janela externa ativa encontrada."))
                }
            } catch (e: Exception) {
                updateState { copy(isScanningScreen = false) }
                OverlayBridge.aiStatusMessage.value = "Erro no escaneamento"
                addLog("Erro ao ler tela: ${e.message}", LogLevel.ERROR)
            }
        }
    }

    /**
     * Smoothly guides the visual mouse pointer across the screen to the target element's coordinates,
     * performs the click pulse animation and optionally dispatches an accessibility click.
     */
    fun moveMouseToElement(element: DetectedUiElement, performClick: Boolean = true) {
        val startX = currentState.targetX
        val startY = currentState.targetY
        val targetX = element.centerX
        val targetY = element.centerY

        updateState {
            copy(
                selectedScreenElement = element,
                targetX = targetX,
                targetY = targetY
            )
        }

        OverlayBridge.sendCommand(
            OverlayBridge.OverlayCommand.MovePointerSmoothly(
                startX = startX,
                startY = startY,
                endX = targetX,
                endY = targetY,
                durationMs = 380L
            )
        )

        val targetName = "${element.text} ${element.contentDescription}".trim().ifEmpty { element.className }
        OverlayBridge.aiStatusMessage.value = "Movendo para '$targetName'"
        addLog("Cursor movido suavemente para ($targetX, $targetY) - '$targetName'", LogLevel.INFO)

        viewModelScope.launch {
            delay(420L) // Wait for glide animation to conclude
            if (performClick) {
                triggerClickFeedback()
                if (currentState.isAccessibilityServiceActive) {
                    AutoClickAccessibilityService.clickAt(targetX.toFloat(), targetY.toFloat())
                }
                addLog("Clique executado no elemento '$targetName'", LogLevel.SUCCESS)

                // Record autonomous action in Room
                val actionEntity = AutonomousActionEntity(
                    actionType = "CLICK_TARGET",
                    startX = startX,
                    startY = startY,
                    targetX = targetX,
                    targetY = targetY,
                    targetLabel = targetName,
                    reason = "Alvo interativo selecionado pelo cérebro autônomo",
                    successful = true
                )
                repository.recordAutonomousAction(actionEntity)
            }
        }
    }

    // =========================================================================
    // REAL-TIME FOREX, ARBITRAGE & TIMING ENGINE
    // =========================================================================

    fun selectCurrencyPair(pair: String) {
        updateState { copy(selectedCurrencyPair = pair) }
        val quote = currentState.currencyQuotes[pair]
        if (quote != null) {
            val ticker = "${quote.pair} ${"%.4f".format(quote.ask)} | Spread: +${"%.1f".format(quote.spreadCentavos)}¢"
            OverlayBridge.spreadTickerInfo.value = ticker
        }
    }

    fun setSimulatedTradeAmount(amount: Double) {
        updateState { copy(simulatedTradeAmount = amount) }
    }

    fun setProfitGoalCentavos(goal: Double) {
        updateState { copy(profitGoalCentavos = goal) }
    }

    fun executeArbitrageConversion(opportunity: ArbitrageOpportunity) {
        val profit = opportunity.profitCentavos
        updateState {
            copy(
                stats = stats.copy(
                    totalArbitrageProfitCentavos = stats.totalArbitrageProfitCentavos + profit,
                    successfulExecutions = stats.successfulExecutions + 1
                )
            )
        }

        // Record Signal in Room
        val signalEntity = ArbitrageSignalEntity(
            basePair = currentState.selectedCurrencyPair,
            route = opportunity.routeName,
            direction = "CONVERTER",
            currentRate = currentState.currencyQuotes[currentState.selectedCurrencyPair]?.ask ?: 5.68,
            targetRate = currentState.currencyQuotes[currentState.selectedCurrencyPair]?.bid ?: 5.66,
            estimatedProfitCentavos = profit,
            confidencePercent = opportunity.confidence,
            timingMoment = "MOMENTO IDEAL (Centavos Favoráveis)",
            executed = true
        )

        viewModelScope.launch {
            repository.saveArbitrageSignal(signalEntity)
            awardXpToProfession("prof_forex_arbitrage", 80)
        }

        addLog("Arbitragem executada na rota [${opportunity.routeName}]. Lucro apurado: +${"%.2f".format(profit)} centavos!", LogLevel.SUCCESS)
        sendEffect(MainUiEffect.ShowSnackbar("Conversão cambial de R$ ${"%.2f".format(opportunity.baseAmount)} concluída com +${"%.2f".format(profit)}¢ de lucro!"))
    }

    /**
     * Toggles the fully autonomous Trader Patrol loop.
     * When running, the AI continuously scans the screen over external apps (e.g. Wise/Banking),
     * evaluates the spread in cents, smoothly glides the mouse cursor to the optimal conversion button,
     * executes the click, and accumulates profits in the Room Database.
     */
    fun toggleAutonomousPatrol() {
        val willRun = !currentState.isAutonomousPatrolRunning
        updateState { copy(isAutonomousPatrolRunning = willRun) }
        OverlayBridge.isAutonomousPatrolRunning.value = willRun

        if (willRun) {
            OverlayBridge.aiStatusMessage.value = "🤖 Trader Autônomo Ativo"
            addLog("Patrulha autônoma de câmbio iniciada: monitorando spread e nós de tela em tempo real.", LogLevel.SUCCESS)
            sendEffect(MainUiEffect.ShowSnackbar("Trader Autônomo Ativado!"))

            autonomousPatrolJob?.cancel()
            autonomousPatrolJob = viewModelScope.launch {
                while (isActive && currentState.isAutonomousPatrolRunning) {
                    delay(3000L)

                    // Step 1: Scan active screen if accessibility is on
                    if (currentState.isAccessibilityServiceActive) {
                        val result = ScreenVisionScanner.scanActiveWindow()
                        if (result != null) {
                            updateState { copy(lastScreenAnalysis = result) }
                            val bestTarget = result.bestActionTarget
                            if (bestTarget != null) {
                                val targetName = "${bestTarget.text} ${bestTarget.contentDescription}".trim()
                                OverlayBridge.aiStatusMessage.value = "Ajustando cursor em '$targetName'"
                                moveMouseToElement(bestTarget, performClick = true)
                            }
                        }
                    }

                    // Step 2: Evaluate Arbitrage Opportunities
                    val bestOpp = currentState.arbitrageOpportunities.maxByOrNull { it.profitCentavos }
                    if (bestOpp != null && bestOpp.profitCentavos >= currentState.profitGoalCentavos) {
                        executeArbitrageConversion(bestOpp)
                    }
                }
            }
        } else {
            autonomousPatrolJob?.cancel()
            OverlayBridge.aiStatusMessage.value = "IA Autônoma Pronta"
            addLog("Patrulha autônoma pausada.", LogLevel.INFO)
            sendEffect(MainUiEffect.ShowSnackbar("Trader Autônomo Pausado."))
        }
    }

    fun clearScreenMemories() {
        viewModelScope.launch {
            repository.clearScreenMemories()
            addLog("Histórico de leituras de tela limpo no banco Room.", LogLevel.INFO)
        }
    }

    fun clearArbitrageSignals() {
        viewModelScope.launch {
            repository.clearSignals()
            addLog("Histórico de sinais de arbitragem limpo no banco Room.", LogLevel.INFO)
        }
    }

    fun toggleQuantumMeeting(): Boolean {
        val active = quantumEngine.toggleMeetingActive()
        if (active) {
            startQuantumCouncilLoop()
            addLog("Sala de Reunião Quântica ATIVADA: funcionários autônomos debatendo e calculando.", LogLevel.SUCCESS)
        } else {
            quantumCouncilJob?.cancel()
            addLog("Sala de Reunião Quântica PAUSADA.", LogLevel.INFO)
        }
        return active
    }

    fun manuallyRecruitDoctor(name: String, role: SpecialistRole, formula: String) {
        val newDoctor = quantumEngine.manuallyRecruitSpecialist(name, role, formula)
        addLog("Novo doutor adicionado à Sala Quântica: ${newDoctor.name} (${newDoctor.role.title})", LogLevel.SUCCESS)
        sendEffect(MainUiEffect.ShowSnackbar("Cientista ${newDoctor.name} conectado com sucesso!"))
    }

    private fun initVoiceAssistant() {
        voiceAssistantEngine.onCommandTriggered = { voiceAction ->
            when (voiceAction) {
                VoiceCommandAction.STOP_ALL -> {
                    emergencyStopAll()
                }
                VoiceCommandAction.START_GAME -> {
                    setTargetCps(50)
                    startAutomation()
                    addLog("Comando de Voz: 'ia jogar esse jogo' - Modo gamer ativado com 50 CPS!", LogLevel.SUCCESS)
                }
                VoiceCommandAction.START_CLICKER -> {
                    startAutomation()
                }
                VoiceCommandAction.PAUSE_CLICKER -> {
                    pauseAutomation()
                }
                VoiceCommandAction.MAX_SPEED_1000 -> {
                    setTargetCps(1000)
                    setClickRateMode(ClickRateMode.CPS)
                    addLog("Comando de Voz: 'ia velocidade mil' - Modo 1000 CPS ativado!", LogLevel.SUCCESS)
                }
                VoiceCommandAction.SWITCH_CAMERA -> {
                    switchCameraFacing()
                }
                VoiceCommandAction.LIVE_STATUS -> {
                    addLog("Comando de Voz: Status solicitado. Status: ${currentState.status.name}, CPS: ${currentState.targetCps}", LogLevel.INFO)
                }
            }
        }

        viewModelScope.launch {
            voiceAssistantEngine.isListening.collectLatest { listening ->
                updateState { copy(isVoiceAssistantListening = listening) }
                OverlayBridge.isVoiceListeningActive.value = listening
            }
        }

        viewModelScope.launch {
            voiceAssistantEngine.lastHeardText.collectLatest { heard ->
                updateState { copy(lastVoiceCommandHeard = heard) }
                OverlayBridge.lastVoiceCommandHeard.value = heard
            }
        }

        viewModelScope.launch {
            voiceAssistantEngine.lastVoiceResponse.collectLatest { resp ->
                updateState { copy(lastVoiceSpokenResponse = resp) }
            }
        }

        viewModelScope.launch {
            voiceAssistantEngine.audioEnergyLevel.collectLatest { level ->
                updateState { copy(audioEnergyLevel = level) }
            }
        }
    }

    /**
     * Emergency Kill Switch: IMMEDIATELY halts all clicking loops, accessibility dispatches,
     * resets state to IDLE, and audibly confirms via voice.
     */
    fun emergencyStopAll() {
        automationJob?.cancel()
        automationJob = null
        autonomousRoutineJob?.cancel()
        autonomousRoutineJob = null
        autonomousPatrolJob?.cancel()
        autonomousPatrolJob = null

        AutoClickAccessibilityService.clearAllGestures()

        updateState {
            copy(
                status = AutomationStatus.IDLE,
                completedClicks = 0,
                isAutonomousRoutineActive = false,
                isAutonomousPatrolRunning = false
            )
        }

        OverlayBridge.automationStatus.value = AutomationStatus.IDLE
        OverlayBridge.clickCounter.value = 0
        OverlayBridge.aiStatusMessage.value = "IA PARADA EM EMERGÊNCIA"

        voiceAssistantEngine.speakAloud("Automação interrompida imediatamente! Estou completamente parado, mestre.")
        addLog("🚨 PARADA DE EMERGÊNCIA ACIONADA! Todos os cliques, rotinas e serviços foram travados com segurança.", LogLevel.ERROR)
        sendEffect(MainUiEffect.ShowSnackbar("🚨 PARADA DE EMERGÊNCIA: Todos os cliques travados!"))
    }

    fun startVoiceListening() {
        voiceAssistantEngine.startListening247()
        updateState { copy(isVoiceAssistantListening = true) }
        OverlayBridge.isVoiceListeningActive.value = true
        addLog("Escuta contínua de voz 24/7 iniciada. Fale 'ia parar' a qualquer momento para travar tudo.", LogLevel.INFO)
    }

    fun stopVoiceListening() {
        voiceAssistantEngine.stopListening()
        updateState { copy(isVoiceAssistantListening = false) }
        OverlayBridge.isVoiceListeningActive.value = false
        addLog("Escuta de voz pausada.", LogLevel.INFO)
    }

    fun toggleVoiceAudible(enabled: Boolean) {
        voiceAssistantEngine.isAudibleVoiceEnabled = enabled
        updateState { copy(isVoiceAudibleEnabled = enabled) }
        OverlayBridge.isTtsAudibleActive.value = enabled
        if (enabled) {
            voiceAssistantEngine.speakAloud("Voz audível ativada. Agora vou falar com você em alto e bom som.")
        }
    }

    fun switchCameraFacing() {
        val newFacing = !currentState.cameraFacingFront
        updateState { copy(cameraFacingFront = newFacing) }
        val label = if (newFacing) "Câmera Frontal (Selfie)" else "Câmera Traseira"
        voiceAssistantEngine.speakAloud("Alternando para $label.")
        addLog("Câmera ao vivo alternada para: $label", LogLevel.INFO)
    }

    override fun onCleared() {
        super.onCleared()
        automationJob?.cancel()
        autonomousRoutineJob?.cancel()
        autonomousPatrolJob?.cancel()
        forexTickerJob?.cancel()
        quantumCouncilJob?.cancel()
        voiceAssistantEngine.destroy()
    }
}
