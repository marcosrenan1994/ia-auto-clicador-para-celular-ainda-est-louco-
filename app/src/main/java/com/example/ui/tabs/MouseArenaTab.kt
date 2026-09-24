package com.example.ui.tabs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PointerStyle
import com.example.viewmodel.MainUiState
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MouseArenaTab(
    uiState: MainUiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val rippleScale = remember { Animatable(0f) }
    val rippleAlpha = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Trigger visual shockwave whenever a click occurs
    LaunchedEffect(uiState.lastClickPulseTime) {
        if (uiState.lastClickPulseTime > 0) {
            rippleScale.snapTo(0.2f)
            rippleAlpha.snapTo(1f)
            scope.launch {
                rippleScale.animateTo(2.2f, animationSpec = tween(350, easing = FastOutSlowInEasing))
            }
            scope.launch {
                rippleAlpha.animateTo(0f, animationSpec = tween(350, easing = FastOutSlowInEasing))
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pointer Style Selector Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("pointer_style_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Estilo do Ponteiro de Mouse",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Toggle pointer visibility
                    Switch(
                        checked = uiState.isMousePointerVisible,
                        onCheckedChange = { viewModel.setPointerVisible(it) },
                        modifier = Modifier.testTag("toggle_pointer_visibility_switch")
                    )
                }

                Text(
                    text = "Escolha o formato visual do mouse para visualizar exatamente onde o autoclicker atuará:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PointerStyle.entries.forEach { style ->
                        val isSelected = uiState.pointerStyle == style
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setPointerStyle(style) },
                            label = { Text(style.displayName, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f).testTag("pointer_chip_${style.name}")
                        )
                    }
                }
            }
        }

        // Interactive Screen Arena / Touchpad
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("interactive_arena_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)) // High-tech dark canvas
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF090D16))
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            viewModel.setTargetCoordinates(offset.x.toInt(), offset.y.toInt())
                            viewModel.performManualTest()
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val newX = (uiState.targetX + dragAmount.x).toInt()
                            val newY = (uiState.targetY + dragAmount.y).toInt()
                            viewModel.setTargetCoordinates(newX, newY)
                        }
                    }
            ) {
                // Background Coordinate Grid
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = 40.dp.toPx()
                    for (x in 0..(size.width / step).toInt()) {
                        drawLine(
                            color = Color(0x1A64748B),
                            start = Offset(x * step, 0f),
                            end = Offset(x * step, size.height),
                            strokeWidth = 1f
                        )
                    }
                    for (y in 0..(size.height / step).toInt()) {
                        drawLine(
                            color = Color(0x1A64748B),
                            start = Offset(0f, y * step),
                            end = Offset(size.width, y * step),
                            strokeWidth = 1f
                        )
                    }

                    // Target Cross Lines intersecting current coordinates
                    val targetOffset = Offset(uiState.targetX.toFloat(), uiState.targetY.toFloat())
                    drawLine(
                        color = Color(0x3338BDF8),
                        start = Offset(0f, targetOffset.y),
                        end = Offset(size.width, targetOffset.y),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = Color(0x3338BDF8),
                        start = Offset(targetOffset.x, 0f),
                        end = Offset(targetOffset.x, size.height),
                        strokeWidth = 1.5f
                    )

                    // Click shockwave ripple
                    if (rippleAlpha.value > 0.01f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x6638BDF8), Color(0x0038BDF8)),
                                center = targetOffset,
                                radius = 60.dp.toPx() * rippleScale.value
                            ),
                            radius = 60.dp.toPx() * rippleScale.value,
                            center = targetOffset
                        )
                        drawCircle(
                            color = Color(0xFF38BDF8).copy(alpha = rippleAlpha.value),
                            radius = 45.dp.toPx() * rippleScale.value,
                            center = targetOffset,
                            style = Stroke(width = 3f)
                        )
                    }
                }

                // Interactive Visible Mouse Cursor overlaying the target
                if (uiState.isMousePointerVisible) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(uiState.targetX - 24, uiState.targetY - 24) }
                            .size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        RenderPointerGraphic(style = uiState.pointerStyle)
                    }
                }

                // Coordinate HUD Badge (Top-Left)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    color = Color(0xCC0F172A),
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                        )
                        Text(
                            text = "Alvo: X = ${uiState.targetX}  |  Y = ${uiState.targetY}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF1F5F9)
                            )
                        )
                    }
                }

                // Interactive Hint Badge (Bottom-Center)
                Surface(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.BottomCenter),
                    color = Color(0x990F172A),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Arraste o mouse para posicionar • Toque para testar clique",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Bio-Jitter Humanization Card (Anti-Detecção)
        Card(
            modifier = Modifier.fillMaxWidth().testTag("jitter_settings_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Jitter Gaussiano Anti-Detecção",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Variação biológica estocástica no intervalo de clique",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = uiState.jitterEnabled,
                        onCheckedChange = { viewModel.toggleJitter(it) },
                        modifier = Modifier.testTag("jitter_switch")
                    )
                }

                if (uiState.jitterEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Dispersão estocástica (σ)", style = MaterialTheme.typography.bodySmall)
                        Text("±${uiState.jitterRangeMs} ms", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Slider(
                        value = uiState.jitterRangeMs.toFloat(),
                        onValueChange = { viewModel.setJitterRange(it.toLong()) },
                        valueRange = 10f..150f,
                        modifier = Modifier.testTag("jitter_slider")
                    )
                }
            }
        }
    }
}

@Composable
private fun RenderPointerGraphic(style: PointerStyle) {
    when (style) {
        PointerStyle.MOUSE_ARROW -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val p = Path().apply {
                    moveTo(12f, 12f)
                    lineTo(12f, 38f)
                    lineTo(20f, 30f)
                    lineTo(28f, 44f)
                    lineTo(34f, 40f)
                    lineTo(26f, 26f)
                    lineTo(36f, 26f)
                    close()
                }
                drawPath(p, color = Color.White)
                drawPath(p, color = Color(0xFF0F172A), style = Stroke(width = 3f))
            }
        }
        PointerStyle.TACTICAL_CROSSHAIR -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                drawCircle(color = Color(0xFFEF4444), radius = 10.dp.toPx(), style = Stroke(width = 2.5f))
                drawCircle(color = Color(0xFFEF4444), radius = 3.dp.toPx())
                drawLine(Color(0xFFEF4444), Offset(center.x - 18.dp.toPx(), center.y), Offset(center.x - 12.dp.toPx(), center.y), strokeWidth = 3f)
                drawLine(Color(0xFFEF4444), Offset(center.x + 12.dp.toPx(), center.y), Offset(center.x + 18.dp.toPx(), center.y), strokeWidth = 3f)
                drawLine(Color(0xFFEF4444), Offset(center.x, center.y - 18.dp.toPx()), Offset(center.x, center.y - 12.dp.toPx()), strokeWidth = 3f)
                drawLine(Color(0xFFEF4444), Offset(center.x, center.y + 12.dp.toPx()), Offset(center.x, center.y + 18.dp.toPx()), strokeWidth = 3f)
            }
        }
        PointerStyle.CYBER_RETICLE -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                drawCircle(color = Color(0xFF38BDF8), radius = 12.dp.toPx(), style = Stroke(width = 2f))
                drawCircle(color = Color(0xFFA855F7), radius = 5.dp.toPx(), style = Stroke(width = 2f))
                drawCircle(color = Color(0xFF38BDF8), radius = 2.dp.toPx())
            }
        }
        PointerStyle.PULSE_BULLSEYE -> {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                drawCircle(color = Color(0xFF10B981), radius = 14.dp.toPx(), style = Stroke(width = 2f))
                drawCircle(color = Color(0xFF10B981), radius = 8.dp.toPx(), style = Stroke(width = 2f))
                drawCircle(color = Color(0xFF10B981), radius = 3.dp.toPx())
            }
        }
    }
}
