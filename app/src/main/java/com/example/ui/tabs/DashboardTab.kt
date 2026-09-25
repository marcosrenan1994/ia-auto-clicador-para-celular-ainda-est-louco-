package com.example.ui.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClickRateMode
import com.example.model.ExtendedClickType
import com.example.model.MouseButtonType
import com.example.viewmodel.ActionType
import com.example.viewmodel.AutomationStatus
import com.example.viewmodel.LogEntry
import com.example.viewmodel.LogLevel
import com.example.viewmodel.MainUiState
import com.example.viewmodel.MainViewModel

@Composable
fun DashboardTab(
    uiState: MainUiState,
    viewModel: MainViewModel,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize().testTag("dashboard_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status & Progress Overview Card
        item(key = "status_hero_card") {
            StatusHeroCard(uiState = uiState)
        }

        // Accessibility Service Reminder if not active
        if (!uiState.isAccessibilityServiceActive) {
            item(key = "accessibility_quick_alert") {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("dashboard_a11y_banner"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4C1D95).copy(alpha = 0.25f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.TouchApp, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Cliques Reais no Chrome, Roblox e Jogos", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFA78BFA)))
                            Text("Para toques físicos fora deste app, habilite o serviço de Acessibilidade no Android.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { viewModel.openAccessibilitySettings() },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Ativar", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Action Control Center (Play, Pause, Stop, Emergency Kill Switch, Test)
        item(key = "voice_live_banner") {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("dashboard_voice_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isVoiceAssistantListening) Color(0xFF10B981) else Color(0xFF334155)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Comandos de Voz 24/7 & Câmeras",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "Diga \"ia parar\" a qualquer momento para travar os cliques.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Button(
                        onClick = { viewModel.setTab(com.example.viewmodel.AppTab.LIVE_CAMERA_VOICE) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Ver Live", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        item(key = "action_controls_card") {
            ActionControlsCard(
                uiState = uiState,
                onStart = { viewModel.startAutomation() },
                onPause = { viewModel.pauseAutomation() },
                onStop = { viewModel.stopAutomation() },
                onEmergencyStop = { viewModel.emergencyStopAll() },
                onTestClick = { viewModel.performManualTest() }
            )
        }

        // Microsoft Store 1000 CPS & Rate Mode Engine Card
        item(key = "ms_rate_engine_card") {
            MicrosoftRateEngineCard(
                uiState = uiState,
                onModeChange = { viewModel.setClickRateMode(it) },
                onCpsChange = { viewModel.setTargetCps(it) },
                onTimeChange = { h, m, s, ms -> viewModel.setExactTime(h, m, s, ms) }
            )
        }

        // Mouse Buttons & Click Types Card
        item(key = "mouse_config_card") {
            MouseButtonsAndTypeCard(
                mouseButton = uiState.mouseButton,
                extendedClickType = uiState.extendedClickType,
                holdDurationMs = uiState.holdDurationMs,
                onSelectButton = { viewModel.setMouseButton(it) },
                onSelectType = { viewModel.setExtendedClickType(it) },
                onHoldDurationChange = { viewModel.setHoldDuration(it) }
            )
        }

        // Anti-Detection Stealth Jitter (Interval & Coordinate Radius)
        item(key = "stealth_jitter_card") {
            StealthAntiDetectionCard(
                jitterEnabled = uiState.jitterEnabled,
                jitterMs = uiState.jitterRangeMs,
                coordRadiusPx = uiState.coordinateJitterRadiusPx,
                onToggleJitter = { viewModel.toggleJitter(it) },
                onJitterMsChange = { viewModel.setJitterRange(it) },
                onCoordRadiusChange = { viewModel.setCoordinateJitterRadius(it) }
            )
        }

        // Multi-Point Sequence Summary Card
        item(key = "multi_point_summary_card") {
            MultiPointSummaryCard(
                uiState = uiState,
                onToggleMultiPoint = { viewModel.toggleMultiPointMode(it) },
                onAddPoint = { viewModel.addMultiPoint() },
                onRemoveLast = { viewModel.removeLastMultiPoint() }
            )
        }

        // Repetition Limit Configuration
        item(key = "repetition_config_card") {
            RepetitionConfigurationCard(
                repeatCount = uiState.repeatCount,
                onRepeatCountChange = { viewModel.setRepeatCount(it) }
            )
        }

        // Hardware Feedback Preferences
        item(key = "hardware_feedback_card") {
            HardwareFeedbackCard(
                vibrationEnabled = uiState.vibrationEnabled,
                soundEnabled = uiState.soundEnabled,
                onToggleVibration = { viewModel.toggleVibration(it) },
                onToggleSound = { viewModel.toggleSound(it) }
            )
        }

        // Execution Stats Card
        item(key = "execution_stats_card") {
            ExecutionStatsCard(stats = uiState.stats)
        }

        // Real-Time Log Section Header
        item(key = "logs_header") {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Histórico de Execução em Tempo Real",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (uiState.logs.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearLogs() },
                        modifier = Modifier.size(32.dp).testTag("clear_logs_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Limpar Histórico",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Log Items
        items(uiState.logs, key = { it.id }) { log ->
            LogItemCard(logEntry = log)
        }
    }
}

@Composable
private fun StatusHeroCard(uiState: MainUiState) {
    val statusColor = when (uiState.status) {
        AutomationStatus.IDLE -> Color(0xFF94A3B8)
        AutomationStatus.RUNNING -> Color(0xFF10B981)
        AutomationStatus.PAUSED -> Color(0xFFF59E0B)
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("status_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Text(
                        text = when (uiState.status) {
                            AutomationStatus.IDLE -> "Automação Ociosa"
                            AutomationStatus.RUNNING -> "Automação Ativa em Execução"
                            AutomationStatus.PAUSED -> "Automação Pausada"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Measured CPS Gauge
                Surface(
                    color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "⚡ ${String.format("%.0f", uiState.measuredLiveCps)} CPS Real",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E5FF)
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Click Count & Progress
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${uiState.completedClicks}",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    Text(
                        text = if (uiState.repeatCount == 0) "de ∞ (Infinito)" else "de ${uiState.repeatCount} toques",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                if (uiState.repeatCount > 0) {
                    val progress = (uiState.completedClicks.toFloat() / uiState.repeatCount.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionControlsCard(
    uiState: MainUiState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onEmergencyStop: () -> Unit,
    onTestClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("action_controls_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Comandos de Ação",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                when (uiState.status) {
                    AutomationStatus.RUNNING -> {
                        Button(
                            onClick = onPause,
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pausar")
                        }
                    }
                    AutomationStatus.PAUSED, AutomationStatus.IDLE -> {
                        Button(
                            onClick = onStart,
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (uiState.status == AutomationStatus.PAUSED) "Continuar" else "Iniciar")
                        }
                    }
                }

                Button(
                    onClick = onStop,
                    enabled = uiState.status != AutomationStatus.IDLE || uiState.isAutonomousRoutineActive,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Parar")
                }
            }

            // Standalone Emergency Killswitch Button (Always Active!)
            Button(
                onClick = onEmergencyStop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("action_controls_emergency_stop"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🚨 PARAR TUDO AGORA (KILL SWITCH)",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color.White)
                )
            }

            OutlinedButton(
                onClick = onTestClick,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Disparar 1 Toque de Teste")
            }
        }
    }
}

/**
 * Microsoft Store 1000 CPS Engine & Exact Timings Card.
 */
@Composable
private fun MicrosoftRateEngineCard(
    uiState: MainUiState,
    onModeChange: (ClickRateMode) -> Unit,
    onCpsChange: (Int) -> Unit,
    onTimeChange: (Int, Int, Int, Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("rate_engine_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                    Text("Velocidade de Toques (Até 1000 CPS)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                }

                Surface(
                    color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (uiState.rateMode == ClickRateMode.CPS) "${uiState.targetCps} CPS" else "${uiState.intervalMillis}ms",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF)),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Mode Selector Tabs
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.rateMode == ClickRateMode.CPS,
                    onClick = { onModeChange(ClickRateMode.CPS) },
                    label = { Text("⚡ Modo CPS (1 a 1000)") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = uiState.rateMode == ClickRateMode.EXACT_TIME,
                    onClick = { onModeChange(ClickRateMode.EXACT_TIME) },
                    label = { Text("⏱️ Tempo Exato (ms)") },
                    modifier = Modifier.weight(1f)
                )
            }

            if (uiState.rateMode == ClickRateMode.CPS) {
                // CPS Slider (1 to 1000 CPS)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Taxa por Segundo:", style = MaterialTheme.typography.bodySmall)
                        Text("${uiState.targetCps} CPS", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF)))
                    }

                    Slider(
                        value = uiState.targetCps.toFloat(),
                        onValueChange = { onCpsChange(it.toInt()) },
                        valueRange = 1f..1000f,
                        steps = 99
                    )

                    // Quick CPS Presets
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(10, 50, 100, 500, 1000).forEach { cps ->
                            FilterChip(
                                selected = uiState.targetCps == cps,
                                onClick = { onCpsChange(cps) },
                                label = { Text(if (cps == 1000) "⚡ 1000" else "$cps", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            } else {
                // Exact Time Interval Controls
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Milissegundos entre cada clique:", style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = uiState.intervalMillis.toFloat(),
                        onValueChange = { onTimeChange(0, 0, 0, it.toLong()) },
                        valueRange = 1f..2000f,
                        steps = 99
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(1L, 10L, 50L, 250L, 1000L).forEach { ms ->
                            FilterChip(
                                selected = uiState.intervalMillis == ms,
                                onClick = { onTimeChange(0, 0, 0, ms) },
                                label = { Text("${ms}ms", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Mouse Buttons and Extended Click Types Card.
 */
@Composable
private fun MouseButtonsAndTypeCard(
    mouseButton: MouseButtonType,
    extendedClickType: ExtendedClickType,
    holdDurationMs: Long,
    onSelectButton: (MouseButtonType) -> Unit,
    onSelectType: (ExtendedClickType) -> Unit,
    onHoldDurationChange: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("mouse_buttons_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Mouse, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Text("Botão e Tipo de Clique", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            }

            // Mouse Buttons
            Text("Botão do Mouse:", style = MaterialTheme.typography.labelSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MouseButtonType.entries.forEach { btn ->
                    FilterChip(
                        selected = mouseButton == btn,
                        onClick = { onSelectButton(btn) },
                        label = { Text(btn.displayName.split(" ")[0] + " " + btn.displayName.split(" ")[1], fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Click Type
            Text("Tipo de Disparo:", style = MaterialTheme.typography.labelSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ExtendedClickType.entries.forEach { type ->
                    FilterChip(
                        selected = extendedClickType == type,
                        onClick = { onSelectType(type) },
                        label = { Text(type.displayName.split(" ")[1], fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (extendedClickType == ExtendedClickType.HOLD_PRESS) {
                Text("Duração do Pressionamento: ${holdDurationMs}ms", style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = holdDurationMs.toFloat(),
                    onValueChange = { onHoldDurationChange(it.toLong()) },
                    valueRange = 100f..2000f,
                    steps = 19
                )
            }
        }
    }
}

/**
 * Humanized Anti-Detection Stealth Jitter Card.
 */
@Composable
private fun StealthAntiDetectionCard(
    jitterEnabled: Boolean,
    jitterMs: Long,
    coordRadiusPx: Int,
    onToggleJitter: (Boolean) -> Unit,
    onJitterMsChange: (Long) -> Unit,
    onCoordRadiusChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("stealth_jitter_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                    Column {
                        Text("Anti-Detecção & Jitter Humano", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text("Variação orgânica para evitar bans em jogos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Switch(checked = jitterEnabled, onCheckedChange = onToggleJitter)
            }

            if (jitterEnabled) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Desvio de Intervalo: ±${jitterMs}ms", style = MaterialTheme.typography.bodySmall)
                    }
                    Slider(
                        value = jitterMs.toFloat(),
                        onValueChange = { onJitterMsChange(it.toLong()) },
                        valueRange = 5f..150f
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Raio de Variação de Coordenada: ±${coordRadiusPx}px", style = MaterialTheme.typography.bodySmall)
                    }
                    Slider(
                        value = coordRadiusPx.toFloat(),
                        onValueChange = { onCoordRadiusChange(it.toInt()) },
                        valueRange = 0f..25f
                    )
                }
            }
        }
    }
}

/**
 * Multi-Point Target Sequence Summary Card.
 */
@Composable
private fun MultiPointSummaryCard(
    uiState: MainUiState,
    onToggleMultiPoint: (Boolean) -> Unit,
    onAddPoint: () -> Unit,
    onRemoveLast: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("multi_point_summary_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.NearMe, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(20.dp))
                    Column {
                        Text("Modo Multi-Ponto Sequencial", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text("${uiState.multiPoints.size} alvos configurados", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Switch(checked = uiState.multiPointModeActive, onCheckedChange = onToggleMultiPoint)
            }

            if (uiState.multiPoints.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    uiState.multiPoints.forEachIndexed { index, point ->
                        val isCurrent = index == uiState.activeMultiPointIndex
                        Surface(
                            color = if (isCurrent) Color(0xFFA855F7) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "#${point.id} (${point.x}, ${point.y})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.White else Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onAddPoint,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar Alvo", style = MaterialTheme.typography.labelSmall)
                }

                if (uiState.multiPoints.isNotEmpty()) {
                    OutlinedButton(
                        onClick = onRemoveLast,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Remover", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun RepetitionConfigurationCard(
    repeatCount: Int,
    onRepeatCountChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("repetition_config_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Limite de Repetições",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = if (repeatCount == 0) "Sem Limite (Infinito)" else "$repeatCount toques",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0 to "∞ Inf", 50 to "50", 100 to "100", 500 to "500", 1000 to "1000").forEach { (count, label) ->
                    val isSelected = repeatCount == count
                    FilterChip(
                        selected = isSelected,
                        onClick = { onRepeatCountChange(count) },
                        label = { Text(label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HardwareFeedbackCard(
    vibrationEnabled: Boolean,
    soundEnabled: Boolean,
    onToggleVibration: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("hardware_feedback_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Vibration, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text("Vibração Háptica a Cada Clique", style = MaterialTheme.typography.bodyMedium)
                }
                Switch(
                    checked = vibrationEnabled,
                    onCheckedChange = onToggleVibration,
                    modifier = Modifier.testTag("vibration_switch")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text("Feedback Sonoro", style = MaterialTheme.typography.bodyMedium)
                }
                Switch(
                    checked = soundEnabled,
                    onCheckedChange = onToggleSound,
                    modifier = Modifier.testTag("sound_switch")
                )
            }
        }
    }
}

@Composable
private fun ExecutionStatsCard(stats: com.example.viewmodel.ExecutionStats) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("execution_stats_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${stats.totalClicksAllTime}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text("Total de Toques", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${stats.totalSessions}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Text("Sessões Criadas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${stats.successfulExecutions}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
                Text("Ações Executadas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun LogItemCard(logEntry: LogEntry) {
    val levelColor = when (logEntry.level) {
        LogLevel.INFO -> MaterialTheme.colorScheme.onSurfaceVariant
        LogLevel.SUCCESS -> Color(0xFF10B981)
        LogLevel.WARNING -> Color(0xFFF59E0B)
        LogLevel.ERROR -> MaterialTheme.colorScheme.error
    }

    Surface(
        modifier = Modifier.fillMaxWidth().testTag("log_item_${logEntry.id}"),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(levelColor)
            )

            Text(
                text = logEntry.timeFormatted,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Text(
                text = logEntry.message,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
