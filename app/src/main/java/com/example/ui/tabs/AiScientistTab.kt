package com.example.ui.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScientistAnalysis
import com.example.viewmodel.MainUiState
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiScientistTab(
    uiState: MainUiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var showApiKeyInput by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Universal Scientist Persona Hero Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().testTag("ai_scientist_hero_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFF0F172A) // Sleek deep scientific slate
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF6366F1), Color(0xFF38BDF8))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Dr. Nexus OmniSciência",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Cientista Universal & IA de Games Autônomos",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Online Internet Status Badge
                    Surface(
                        color = Color(0xFF064E3B).copy(alpha = 0.6f),
                        shape = RoundedCornerShape(20.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Online Web",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF34D399),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Text(
                    text = "Acesso a todas as ciências do universo: cálculo estocástico, teoria dos jogos, física computacional e busca em tempo real na internet para resolver qualquer jogo autonomamente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1)
                )

                // Optional API Key Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { showApiKeyInput = !showApiKeyInput },
                        modifier = Modifier.testTag("toggle_custom_key_btn")
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (showApiKeyInput) "Ocultar Chave" else "Configurar Chave Gemini", style = MaterialTheme.typography.labelSmall)
                    }
                }

                AnimatedVisibility(visible = showApiKeyInput) {
                    OutlinedTextField(
                        value = uiState.customApiKey,
                        onValueChange = { viewModel.setCustomApiKey(it) },
                        label = { Text("Chave de API Gemini (Opcional - já usa Secrets)") },
                        placeholder = { Text("AIzaSy...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("custom_api_key_input")
                    )
                }
            }
        }

        // Query Input Section
        Card(
            modifier = Modifier.fillMaxWidth().testTag("ai_query_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Missão Científica para a IA:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = uiState.aiGameContext,
                    onValueChange = { viewModel.setAiGameContext(it) },
                    label = { Text("Nome do Jogo ou Aplicativo") },
                    placeholder = { Text("Ex: Cookie Clicker, Roblox, Ragnarok, Tap Titans...") },
                    modifier = Modifier.fillMaxWidth().testTag("ai_game_context_input")
                )

                OutlinedTextField(
                    value = uiState.aiUserQuery,
                    onValueChange = { viewModel.setAiUserQuery(it) },
                    label = { Text("Instrução ou Pergunta de Otimização") },
                    placeholder = { Text("Ex: Calcule o melhor intervalo de CPS e estratégia para vencer...") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("ai_user_query_input")
                )

                // Quick Prompt Presets
                Text(
                    text = "Perguntas Científicas Rápidas:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PresetChip("🚀 CPS Máximo & Latência") {
                        viewModel.setAiUserQuery("Calcule a cadência ótima de CPS sem sobrecarregar a taxa de atualização da tela")
                    }
                    PresetChip("🛡️ Anti-Detecção Gaussiana") {
                        viewModel.setAiUserQuery("Calcule o desvio padrão de Jitter ideal para burlar sistemas anti-macro com toques humanos")
                    }
                    PresetChip("🎮 Estratégia de Jogo Autônomo") {
                        viewModel.setAiUserQuery("Monte um plano de ações autônomas em etapas para farmar e evoluir sozinho")
                    }
                    PresetChip("🎲 Probabilidade de Drops") {
                        viewModel.setAiUserQuery("Calcule a distribuição binomial de acerto de drops raros com 500 cliques contínuos")
                    }
                }

                // Execute AI Consultation Button
                Button(
                    onClick = { viewModel.consultScientist() },
                    enabled = !uiState.isConsultingAi,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_consult_scientist"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isConsultingAi) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("O Cientista está calculando com a Web...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Consultar Inteligência Artificial Cientista")
                    }
                }
            }
        }

        // Display AI Scientist Results if available
        uiState.scientistAnalysis?.let { analysis ->
            ScientistResultsSection(
                analysis = analysis,
                uiState = uiState,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun PresetChip(label: String, onClick: () -> Unit) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        leadingIcon = { Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(14.dp)) }
    )
}

@Composable
private fun ScientistResultsSection(
    analysis: ScientistAnalysis,
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Quick Action: Apply to Autoclicker
        Card(
            modifier = Modifier.fillMaxWidth().testTag("apply_ai_parameters_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Parâmetros Científicos Calculados",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Intervalo: ${analysis.recommendedIntervalMs}ms • Jitter: ±${analysis.recommendedJitterMs}ms • CPS: ${String.format("%.1f", analysis.theoreticalCps)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Button(
                    onClick = { viewModel.applyScientistParameters() },
                    modifier = Modifier.fillMaxWidth().testTag("btn_apply_scientist_params"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aplicar Parâmetros da IA no Autoclicker")
                }
            }
        }

        // Scientific Calculations Cards (Formulas and Mathematical Proofs)
        if (analysis.calculations.isNotEmpty()) {
            Text(
                text = "Formulação Matemática & Cálculos Físicos:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            analysis.calculations.forEach { calc ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(calc.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Text(calc.value, style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold))
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = calc.formula,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(calc.explanation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Autonomous Action Routine
        if (analysis.autonomousPlan.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("autonomous_routine_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Text("Plano de Jogabilidade Autônoma", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    Text(
                        text = "O Cientista gerou uma sequência de passos autônomos para jogar o jogo sem intervenção humana:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    analysis.autonomousPlan.forEachIndexed { idx, step ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (uiState.isAutonomousRoutineActive && uiState.currentAutonomousStep == idx) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(24.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${idx + 1}", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(step.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                    Text(
                                        "Alvo: (${step.targetX}, ${step.targetY}) • ${step.clicks} cliques • ${step.intervalMs}ms",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (uiState.isAutonomousRoutineActive) {
                                viewModel.stopAutomation()
                            } else {
                                viewModel.startAutonomousRoutine()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_execute_autonomous_routine"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isAutonomousRoutineActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            imageVector = if (uiState.isAutonomousRoutineActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (uiState.isAutonomousRoutineActive) "Parar Rotina Autônoma" else "Executar Jogo Autônomo Agora")
                    }
                }
            }
        }

        // Full Scientific Analysis & Synthesis
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Parecer Científico Completo:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(analysis.explanation, style = MaterialTheme.typography.bodyMedium)

                if (analysis.sources.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Fontes e Grounding Web:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    analysis.sources.forEach { source ->
                        Text("• $source", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
