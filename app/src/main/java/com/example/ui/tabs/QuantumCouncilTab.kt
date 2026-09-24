package com.example.ui.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AutonomousStaffMember
import com.example.model.CouncilDialogue
import com.example.model.SpecialistRole
import com.example.viewmodel.MainUiState

@Composable
fun QuantumCouncilTab(
    state: MainUiState,
    onToggleMeeting: () -> Unit,
    onAddDoctor: (name: String, role: SpecialistRole, formula: String) -> Unit
) {
    val meeting = state.quantumMeetingState
    var showRecruitDialog by remember { mutableStateOf(false) }

    // Pulsing neural animation for active meeting room
    val infiniteTransition = rememberInfiniteTransition(label = "quantum_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quantum_council_tab_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card: Sala de Reunião Quântica & Cérebro Conectado
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quantum_meeting_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF8B5CF6), Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFFEC4899))
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "⚛️ Sala Quântica de Funcionários",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = "Cérebros auto-conectados debatendo cálculos, moedas e gráficos em tempo real",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Active Synced Status Pill
                        Surface(
                            shape = CircleShape,
                            color = if (meeting.isMeetingActive) Color(0xFF8B5CF6).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (meeting.isMeetingActive) Color(0xFF8B5CF6) else Color.Gray.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (meeting.isMeetingActive) Color(0xFF8B5CF6) else Color.Gray)
                                )
                                Text(
                                    text = if (meeting.isMeetingActive) "MALHA CONECTADA" else "PAUSADA",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (meeting.isMeetingActive) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Quantum Mesh Neural Visualizer (Animated Interactive Canvas)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val radius = size.height * 0.38f

                            // Concentric pulsing quantum rings
                            drawCircle(
                                color = Color(0xFF8B5CF6).copy(alpha = 0.2f),
                                radius = radius * pulseScale,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f)))
                            )
                            drawCircle(
                                color = Color(0xFF3B82F6).copy(alpha = 0.25f),
                                radius = radius * 0.7f,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = 2f)
                            )

                            // Nodes around the circle representing connected doctors
                            val staffCount = meeting.staff.size.coerceAtLeast(1)
                            for (i in 0 until staffCount) {
                                val angle = Math.toRadians((360.0 / staffCount) * i + (rotationAngle * 0.2)).toFloat()
                                val nodeX = centerX + kotlin.math.cos(angle) * radius
                                val nodeY = centerY + kotlin.math.sin(angle) * radius

                                // Synapse line to core brain
                                drawLine(
                                    brush = Brush.linearGradient(
                                        listOf(Color(0xFF8B5CF6).copy(alpha = 0.5f), Color(0xFF10B981).copy(alpha = 0.7f))
                                    ),
                                    start = Offset(centerX, centerY),
                                    end = Offset(nodeX, nodeY),
                                    strokeWidth = 2f
                                )

                                // Node circle
                                drawCircle(
                                    color = Color(0xFF10B981),
                                    radius = 6f,
                                    center = Offset(nodeX, nodeY)
                                )
                            }

                            // Central Brain Node
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(Color(0xFFEC4899), Color(0xFF8B5CF6)),
                                    center = Offset(centerX, centerY),
                                    radius = 24f
                                ),
                                radius = 18f * pulseScale,
                                center = Offset(centerX, centerY)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🧠",
                                fontSize = 22.sp,
                                modifier = Modifier.scale(pulseScale)
                            )
                            Text(
                                text = "${meeting.staff.size} Cientistas em Malha",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Real-time Counters Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Ciclos Quânticos",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${meeting.quantumCyclesCount}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8B5CF6)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(26.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Freq. Sincronizada",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${"%.1f".format(meeting.syncedBrainFrequencyHz)} Hz",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(26.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Sabedoria Coletiva",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${meeting.collectiveWisdomSynthesized} XP",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B82F6)
                            )
                        }
                    }

                    // Action buttons (Toggle Meeting & Recruit Doctor)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onToggleMeeting,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("toggle_quantum_meeting_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (meeting.isMeetingActive) Color(0xFFDC2626) else Color(0xFF8B5CF6)
                            )
                        ) {
                            Icon(
                                imageVector = if (meeting.isMeetingActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (meeting.isMeetingActive) "Pausar Reunião" else "Ativar Reunião Quântica",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        OutlinedButton(
                            onClick = { showRecruitDialog = true },
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("recruit_doctor_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Conectar Doutor",
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }

        // Section: Funcionários Autônomos Conectados na Sala
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bancada de Cientistas & Operadores (${meeting.staff.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Auto-Adição Ativa ⚡",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(meeting.staff) { member ->
            AutonomousStaffCard(member = member)
        }

        // Section: Diálogo em Tempo Real na Sala de Reunião
        item {
            Text(
                text = "💬 Diálogo & Debate em Tempo Real na Mesa Redonda",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(meeting.dialogues.takeLast(10).reversed()) { dialogue ->
            DialogueBubble(dialogue = dialogue)
        }
    }

    if (showRecruitDialog) {
        RecruitDoctorDialog(
            onDismiss = { showRecruitDialog = false },
            onConfirm = { name, role, formula ->
                onAddDoctor(name, role, formula)
                showRecruitDialog = false
            }
        )
    }
}

@Composable
fun AutonomousStaffCard(member: AutonomousStaffMember) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("staff_card_${member.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (member.isSpeaking) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (member.isSpeaking) Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF10B981))) else Brush.linearGradient(listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.3f)))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = member.avatarEmoji,
                        fontSize = 24.sp
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (member.autoRecruited) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF8B5CF6).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "AUTO",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8B5CF6),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = member.role.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "Nível ${member.level}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Formula / Specialism tag
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "🔬 ${member.specialFormula}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Real-time thought & calculations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "\"${member.currentThought}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (member.isSpeaking) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${member.totalCalculations / 1000}k ops",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }
        }
    }
}

@Composable
fun DialogueBubble(dialogue: CouncilDialogue) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = dialogue.speakerEmoji,
                fontSize = 20.sp
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dialogue.speakerName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = dialogue.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = dialogue.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun RecruitDoctorDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, role: SpecialistRole, formula: String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var formulaInput by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(SpecialistRole.QUANTUM_MATH_SCIENTIST) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Conectar Novo Doutor / Cientista", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Nome do Doutor / Especialista") },
                    placeholder = { Text("Ex: Dr. Euler Cálculos") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Área de Especialidade Quântica:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                SpecialistRole.values().forEach { role ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedRole = role }
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role }
                        )
                        Text(
                            text = "${role.iconEmoji} ${role.title}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (selectedRole == role) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                OutlinedTextField(
                    value = formulaInput,
                    onValueChange = { formulaInput = it },
                    label = { Text("Fórmula Matemática / Algoritmo") },
                    placeholder = { Text("Ex: Min(Spread) ➔ TargetClick") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(nameInput, selectedRole, formulaInput)
                }
            ) {
                Text("Conectar à Sala")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
