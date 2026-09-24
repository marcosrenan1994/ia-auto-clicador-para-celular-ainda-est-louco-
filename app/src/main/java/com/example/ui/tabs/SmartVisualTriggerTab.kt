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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MacroActionType
import com.example.model.MacroStep
import com.example.model.SmartVisualTriggerRule
import com.example.model.VisualTriggerAction
import com.example.model.VisualTriggerCondition
import com.example.viewmodel.MainUiState
import com.example.viewmodel.MainViewModel

/**
 * Smart Visual Triggers & Macro Recorder Tab.
 * Cloned directly from Microsoft Store Auto Clicker apps (9mszwlljjvb1 / 9n31lzkvgqvp):
 * - Image & Color Recognition Trigger Engine
 * - Multi-Step Keyboard & Mouse Macro Recorder & Sequencer
 */
@Composable
fun SmartVisualTriggerTab(
    state: MainUiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var subTab by remember { mutableIntStateOf(0) } // 0 = Visual Triggers, 1 = Macro Recorder

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("smart_visual_trigger_tab")
    ) {
        // Tab Header
        TabRow(
            selectedTabIndex = subTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = subTab == 0,
                onClick = { subTab = 0 },
                text = { Text("🎯 Gatilhos Visuais", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                text = { Text("⏺️ Gravador de Macros", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.FiberManualRecord, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        if (subTab == 0) {
            VisualTriggersContent(state = state, viewModel = viewModel)
        } else {
            MacroRecorderContent(state = state, viewModel = viewModel)
        }
    }
}

@Composable
private fun VisualTriggersContent(
    state: MainUiState,
    viewModel: MainViewModel
) {
    var newRuleName by remember { mutableStateOf("") }
    var selectedHex by remember { mutableStateOf("#10B981") }
    var tolerance by remember { mutableIntStateOf(85) }
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card: Scanner State & Live Radar
        item(key = "visual_scanner_hero") {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("visual_scanner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isVisualTriggerScannerActive) Color(0xFF064E3B).copy(alpha = 0.35f) else Color(0xFF0F172A)
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (state.isVisualTriggerScannerActive) Color(0xFF10B981) else Color(0xFF475569)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radar,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Smart Visual Triggers",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (state.isVisualTriggerScannerActive) "Escaneando tela ativamente..." else "Scanner em pausa",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (state.isVisualTriggerScannerActive) Color(0xFF34D399) else Color(0xFF94A3B8)
                                )
                            }
                        }

                        Switch(
                            checked = state.isVisualTriggerScannerActive,
                            onCheckedChange = { viewModel.toggleVisualTriggerScanner(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF10B981),
                                checkedTrackColor = Color(0xFF064E3B)
                            ),
                            modifier = Modifier.testTag("scanner_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Scanner Info Banner
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Última Deteção Visual:",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                            )
                            Text(
                                text = state.lastVisualTriggerFired.ifBlank { "Nenhum alvo detectado ainda. Ative o scanner para buscar esmeraldas, moedas ou botões." },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = if (state.lastVisualTriggerFired.isNotBlank()) Color(0xFF38BDF8) else Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section Header: Active Rules
        item(key = "rules_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Regras de Reconhecimento (${state.visualTriggerRules.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = { showAddDialog = !showAddDialog },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showAddDialog) "Fechar" else "Nova Regra", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Add New Rule Panel
        if (showAddDialog) {
            item(key = "add_rule_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Criar Gatilho Visual Personalizado", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

                        TextField(
                            value = newRuleName,
                            onValueChange = { newRuleName = it },
                            label = { Text("Nome da Regra (ex: Coletar Esmeralda)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Text("Cor Alvo na Tela:", style = MaterialTheme.typography.labelSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("#10B981", "#F59E0B", "#EF4444", "#3B82F6", "#A855F7").forEach { hex ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(hex)))
                                        .border(
                                            width = if (selectedHex == hex) 3.dp else 1.dp,
                                            color = if (selectedHex == hex) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }

                        Text("Tolerância de Similaridade: $tolerance%", style = MaterialTheme.typography.labelSmall)
                        Slider(
                            value = tolerance.toFloat(),
                            onValueChange = { tolerance = it.toInt() },
                            valueRange = 50f..99f,
                            steps = 49
                        )

                        Button(
                            onClick = {
                                if (newRuleName.isNotBlank()) {
                                    val rule = SmartVisualTriggerRule(
                                        id = "custom_trigger_${System.currentTimeMillis()}",
                                        name = newRuleName,
                                        sampleX = state.targetX,
                                        sampleY = state.targetY,
                                        targetColorHex = selectedHex,
                                        tolerancePercent = tolerance,
                                        condition = VisualTriggerCondition.COLOR_MATCHES,
                                        action = VisualTriggerAction.BURST_1000_CPS,
                                        isEnabled = true
                                    )
                                    viewModel.addVisualTriggerRule(rule)
                                    newRuleName = ""
                                    showAddDialog = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Salvar e Ativar Gatilho")
                        }
                    }
                }
            }
        }

        // List of Visual Trigger Rules
        items(state.visualTriggerRules, key = { it.id }) { rule ->
            VisualTriggerRuleItem(
                rule = rule,
                onToggle = { enabled -> viewModel.toggleVisualTriggerRule(rule.id, enabled) },
                onTestBurst = {
                    viewModel.msManager.executeUltraSpeedBurst(rule.sampleX, rule.sampleY, burstCount = 10)
                }
            )
        }
    }
}

@Composable
private fun VisualTriggerRuleItem(
    rule: SmartVisualTriggerRule,
    onToggle: (Boolean) -> Unit,
    onTestBurst: () -> Unit
) {
    val swatchColor = try {
        Color(android.graphics.Color.parseColor(rule.targetColorHex))
    } catch (_: Exception) {
        Color.Green
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (rule.isEnabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Color sample indicator
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(swatchColor)
                    .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Ação: ${rule.action.displayName} (Tol: ${rule.tolerancePercent}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (rule.triggerCount > 0) {
                    Text(
                        text = "Disparado ${rule.triggerCount} vezes",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF10B981))
                    )
                }
            }

            OutlinedButton(
                onClick = onTestBurst,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Testar", style = MaterialTheme.typography.labelSmall)
            }

            Switch(
                checked = rule.isEnabled,
                onCheckedChange = onToggle,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun MacroRecorderContent(
    state: MainUiState,
    viewModel: MainViewModel
) {
    var macroNameInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Record Control Card
        item(key = "macro_record_card") {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("macro_recorder_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isMacroRecording) Color(0xFF7F1D1D).copy(alpha = 0.35f) else Color(0xFF0F172A)
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (state.isMacroRecording) "Gravando Passos em Tempo Real..." else "Gravador de Sequências (Macro)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = if (state.isMacroRecording) Color(0xFFF87171) else MaterialTheme.colorScheme.onSurface)
                            )
                            Text(
                                text = if (state.isMacroRecording) "${state.recordedMacroSteps.size} passos capturados" else "Grave cliques, gestos e pausas para reprodução automática.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleMacroRecording() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isMacroRecording) Color(0xFFEF4444) else Color(0xFF4F46E5)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (state.isMacroRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (state.isMacroRecording) "Parar & Salvar" else "Gravar")
                        }
                    }

                    if (state.isMacroRecording) {
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Registrar toque na coordenada atual (${state.targetX}, ${state.targetY})", style = MaterialTheme.typography.bodySmall)
                                Button(
                                    onClick = { viewModel.recordMacroStepAt(state.targetX, state.targetY) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("+ Passo", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Macros Header
        item(key = "macros_list_header") {
            Text(
                text = "Macros Prontos (${state.macros.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Macro Cards
        items(state.macros, key = { it.id }) { macro ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(macro.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("${macro.steps.size} passos • Loop: ${if (macro.loopUntilStopped) "Infinito" else "${macro.repeatCount}x"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = {
                                if (state.isMacroPlaying && state.selectedMacro?.id == macro.id) {
                                    viewModel.stopMacro()
                                } else {
                                    viewModel.playMacro(macro)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isMacroPlaying && state.selectedMacro?.id == macro.id) Color(0xFFEF4444) else Color(0xFF10B981)
                            )
                        ) {
                            Icon(
                                imageVector = if (state.isMacroPlaying && state.selectedMacro?.id == macro.id) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (state.isMacroPlaying && state.selectedMacro?.id == macro.id) "Parar" else "Executar", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Steps Preview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        macro.steps.take(4).forEach { step ->
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "#${step.stepIndex} ${step.type.displayName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (macro.steps.size > 4) {
                            Text("+${macro.steps.size - 4}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
