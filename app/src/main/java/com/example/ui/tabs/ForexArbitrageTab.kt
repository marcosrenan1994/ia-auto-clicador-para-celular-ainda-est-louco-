package com.example.ui.tabs

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.forex.ArbitrageOpportunity
import com.example.data.forex.CandleStick
import com.example.data.forex.CurrencyQuote
import com.example.data.forex.TimingSignal
import com.example.data.local.entity.ArbitrageSignalEntity
import com.example.viewmodel.MainUiState

@Composable
fun ForexArbitrageTab(
    state: MainUiState,
    onSelectPair: (String) -> Unit,
    onExecuteArbitrage: (ArbitrageOpportunity) -> Unit,
    onToggleAutonomousPatrol: () -> Unit,
    onClearSignals: () -> Unit
) {
    val selectedQuote = state.currencyQuotes[state.selectedCurrencyPair]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("forex_arbitrage_tab_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header & Autonomous Patrol Controller
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forex_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF10B981), Color(0xFF6366F1), Color(0xFF38BDF8))
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "💱 Câmbio & Arbitragem Wise",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Análise de centavos, spread e timing ideal de compra/venda",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Autonomous Trader Patrol Pill
                        Surface(
                            shape = CircleShape,
                            color = if (state.isAutonomousPatrolRunning) Color(0xFF10B981).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (state.isAutonomousPatrolRunning) Color(0xFF10B981) else Color.Gray.copy(alpha = 0.3f)
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
                                        .background(if (state.isAutonomousPatrolRunning) Color(0xFF10B981) else Color.Gray)
                                )
                                Text(
                                    text = if (state.isAutonomousPatrolRunning) "TRADER ATIVO" else "PATRULHA PAUSADA",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.isAutonomousPatrolRunning) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Profit summary counter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Lucro Acumulado",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "+${"%.2f".format(state.stats.totalArbitrageProfitCentavos)} ¢",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Operações Feitas",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${state.stats.successfulExecutions}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Button(
                        onClick = onToggleAutonomousPatrol,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("toggle_autonomous_patrol_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isAutonomousPatrolRunning) Color(0xFFDC2626) else Color(0xFF10B981)
                        )
                    ) {
                        Icon(
                            imageVector = if (state.isAutonomousPatrolRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isAutonomousPatrolRunning) "Pausar Trader Autônomo" else "Ativar Trader Autônomo com Wise",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Currency Pair Selector Chips
        item {
            Text(
                text = "Pares de Cotação em Tempo Real",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val pairs = listOf("USD/BRL", "EUR/BRL", "GBP/BRL", "EUR/USD", "GBP/USD", "USDT/USD")
                items(pairs) { pair ->
                    val isSelected = state.selectedCurrencyPair == pair
                    val quote = state.currencyQuotes[pair]
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectPair(pair) },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(pair, fontWeight = FontWeight.Bold)
                                if (quote != null) {
                                    Text(
                                        text = "%.4f".format(quote.ask),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        modifier = Modifier.testTag("pair_chip_$pair")
                    )
                }
            }
        }

        // Live Candlestick & Technical Indicator Graph
        if (selectedQuote != null) {
            item {
                QuoteDetailCard(quote = selectedQuote)
            }
        }

        // Triangular Arbitrage Opportunities
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Oportunidades de Arbitragem & Spread",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Spread em Centavos",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        items(state.arbitrageOpportunities) { opp ->
            ArbitrageOpportunityCard(
                opportunity = opp,
                onExecute = { onExecuteArbitrage(opp) }
            )
        }

        // Room Database Historical Signals
        if (state.arbitrageSignalsHistory.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Histórico no Banco de Dados (Room)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onClearSignals) {
                        Text("Limpar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            items(state.arbitrageSignalsHistory.take(8)) { signal ->
                SignalHistoryRow(signal = signal)
            }
        }
    }
}

@Composable
fun QuoteDetailCard(quote: CurrencyQuote) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quote_detail_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = quote.pair,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Venda: ${"%.4f".format(quote.bid)} | Compra: ${"%.4f".format(quote.ask)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Timing recommendation badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(android.graphics.Color.parseColor(quote.timingRecommendation.badgeColorHex)).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(android.graphics.Color.parseColor(quote.timingRecommendation.badgeColorHex))
                    )
                ) {
                    Text(
                        text = quote.timingRecommendation.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(android.graphics.Color.parseColor(quote.timingRecommendation.badgeColorHex)),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Real-time Canvas Candlestick & Price Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(8.dp)
            ) {
                CandleStickCanvas(candles = quote.historyCandles)
            }

            // Metrics Row: Spread in Cents, RSI, EMA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricPill(
                    label = "Diferença Spread",
                    value = "+${"%.2f".format(quote.spreadCentavos)} ¢",
                    color = Color(0xFF10B981)
                )
                MetricPill(
                    label = "RSI (14)",
                    value = "%.1f".format(quote.rsi14),
                    color = if (quote.rsi14 < 30) Color(0xFF10B981) else if (quote.rsi14 > 70) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
                )
                MetricPill(
                    label = "EMA (9)",
                    value = "%.4f".format(quote.ema9),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = "💡 ${quote.timingRecommendation.description}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MetricPill(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun CandleStickCanvas(candles: List<CandleStick>) {
    if (candles.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val minPrice = candles.minOfOrNull { it.low } ?: 0.0
        val maxPrice = candles.maxOfOrNull { it.high } ?: 1.0
        val range = (maxPrice - minPrice).coerceAtLeast(0.0001)

        val candleWidth = size.width / (candles.size.toFloat() + 2f)

        // Draw gridlines
        val gridLines = 3
        for (i in 0..gridLines) {
            val y = (size.height / gridLines) * i
            drawLine(
                color = Color.Gray.copy(alpha = 0.15f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
        }

        // Draw candles
        candles.forEachIndexed { index, candle ->
            val x = (index + 1) * candleWidth
            val isBullish = candle.close >= candle.open
            val candleColor = if (isBullish) Color(0xFF10B981) else Color(0xFFEF4444)

            val openY = size.height - (((candle.open - minPrice) / range) * size.height).toFloat()
            val closeY = size.height - (((candle.close - minPrice) / range) * size.height).toFloat()
            val highY = size.height - (((candle.high - minPrice) / range) * size.height).toFloat()
            val lowY = size.height - (((candle.low - minPrice) / range) * size.height).toFloat()

            // Wick
            drawLine(
                color = candleColor,
                start = Offset(x, highY),
                end = Offset(x, lowY),
                strokeWidth = 2f
            )

            // Body
            val top = minOf(openY, closeY)
            val height = kotlin.math.max(kotlin.math.abs(openY - closeY), 3f)
            drawRect(
                color = candleColor,
                topLeft = Offset(x - (candleWidth * 0.35f), top),
                size = Size(candleWidth * 0.7f, height)
            )
        }
    }
}

@Composable
fun ArbitrageOpportunityCard(
    opportunity: ArbitrageOpportunity,
    onExecute: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arbitrage_card_${opportunity.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = opportunity.routeName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = CircleShape,
                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+${"%.2f".format(opportunity.profitCentavos)} ¢ de Lucro",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Margem: +${"%.2f".format(opportunity.spreadMarginPercent)}% | Confiança: ${opportunity.confidence}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Diferença Wise: ${"%.1f".format(opportunity.wiseComparisonSpread)}¢",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Button(
                onClick = onExecute,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = opportunity.recommendedAction,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SignalHistoryRow(signal: ArbitrageSignalEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = signal.route,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = signal.timingMoment,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "+${"%.2f".format(signal.estimatedProfitCentavos)} ¢",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
        }
    }
}
