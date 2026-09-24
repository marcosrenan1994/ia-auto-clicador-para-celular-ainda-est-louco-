package com.example.data.forex

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

data class CandleStick(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double,
    val timeLabel: String
)

data class CurrencyQuote(
    val pair: String, // e.g. "USD/BRL"
    val baseCurrency: String,
    val targetCurrency: String,
    val bid: Double,
    val ask: Double,
    val spreadCentavos: Double, // in fractional cents
    val dayHigh: Double,
    val dayLow: Double,
    val changePercent: Double,
    val rsi14: Double,
    val ema9: Double,
    val timingRecommendation: TimingSignal,
    val historyCandles: List<CandleStick>
)

enum class TimingSignal(val label: String, val badgeColorHex: String, val description: String) {
    STRONG_BUY("MOMENTO DE COMPRA", "#10B981", "Taxa no fundo técnico e spread reduzido. Ideal para comprar."),
    STRONG_SELL("MOMENTO DE VENDA", "#EF4444", "Pico de centavos alcançado. Momento ótimo para realizar lucro."),
    NEUTRAL_WAIT("AGUARDAR CENTAVOS", "#F59E0B", "Mercado em consolidação. Aguardando expansão do spread."),
    ARBITRAGE_OPPORTUNITY("OPORTUNIDADE DE ARBITRAGEM", "#8B5CF6", "Diferença lucrativa detectada entre cotações cruzadas.")
}

data class ArbitrageOpportunity(
    val id: String,
    val routeName: String, // e.g. "BRL ➔ USD ➔ EUR ➔ BRL"
    val baseAmount: Double,
    val expectedReturn: Double,
    val profitCentavos: Double,
    val spreadMarginPercent: Double,
    val confidence: Int,
    val wiseComparisonSpread: Double,
    val recommendedAction: String
)

class ForexArbitrageEngine {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Base mock baseline rates around real current market baselines
    private var usdBrl = 5.6840
    private var eurBrl = 6.1820
    private var gbpBrl = 7.3450
    private var eurUsd = 1.0875
    private var gbpUsd = 1.2920
    private var usdtUsd = 0.9998

    private val _quotes = MutableStateFlow<Map<String, CurrencyQuote>>(emptyMap())
    val quotes = _quotes.asStateFlow()

    private val _arbitrageOpportunities = MutableStateFlow<List<ArbitrageOpportunity>>(emptyList())
    val arbitrageOpportunities = _arbitrageOpportunities.asStateFlow()

    private val candleHistory = mutableMapOf<String, MutableList<CandleStick>>()

    init {
        initializeHistory()
        tickMarket()
    }

    private fun initializeHistory() {
        val pairs = listOf("USD/BRL", "EUR/BRL", "GBP/BRL", "EUR/USD", "GBP/USD", "USDT/USD")
        val now = System.currentTimeMillis()

        for (pair in pairs) {
            val basePrice = when (pair) {
                "USD/BRL" -> usdBrl
                "EUR/BRL" -> eurBrl
                "GBP/BRL" -> gbpBrl
                "EUR/USD" -> eurUsd
                "GBP/USD" -> gbpUsd
                else -> usdtUsd
            }

            val list = mutableListOf<CandleStick>()
            var current = basePrice * 0.985
            for (i in 20 downTo 0) {
                val t = now - (i * 60_000L)
                val delta = (Random.nextDouble(-0.008, 0.008)) * basePrice
                val open = current
                val close = (open + delta).coerceAtLeast(0.01)
                val high = maxOf(open, close) + (Random.nextDouble(0.001, 0.004) * basePrice)
                val low = minOf(open, close) - (Random.nextDouble(0.001, 0.004) * basePrice)
                current = close

                list.add(
                    CandleStick(
                        timestamp = t,
                        open = open,
                        high = high,
                        low = low,
                        close = close,
                        volume = Random.nextDouble(50_000.0, 450_000.0),
                        timeLabel = timeFormat.format(Date(t))
                    )
                )
            }
            candleHistory[pair] = list
        }
    }

    /**
     * Ticks the market with high-frequency live fluctuations down to the fractional cents.
     */
    fun tickMarket() {
        val now = System.currentTimeMillis()
        val timeLabel = timeFormat.format(Date(now))

        fun updatePair(pair: String, currentBase: Double, volatility: Double): Pair<Double, CurrencyQuote> {
            val delta = (Random.nextDouble(-volatility, volatility))
            val newPrice = (currentBase + delta).coerceAtLeast(0.01)
            val spread = Random.nextDouble(0.0015, 0.0045) // Centavos spread
            val bid = newPrice - (spread / 2.0)
            val ask = newPrice + (spread / 2.0)

            val candles = candleHistory.getOrPut(pair) { mutableListOf() }
            val lastCandle = candles.lastOrNull()

            if (lastCandle == null || (now - lastCandle.timestamp) > 45_000L) {
                // New candle
                candles.add(
                    CandleStick(
                        timestamp = now,
                        open = bid,
                        high = maxOf(bid, ask) + 0.001,
                        low = minOf(bid, ask) - 0.001,
                        close = ask,
                        volume = Random.nextDouble(10_000.0, 100_000.0),
                        timeLabel = timeLabel
                    )
                )
                if (candles.size > 30) candles.removeAt(0)
            } else {
                // Update current candle
                val updatedCandle = lastCandle.copy(
                    high = maxOf(lastCandle.high, ask),
                    low = minOf(lastCandle.low, bid),
                    close = ask,
                    volume = lastCandle.volume + Random.nextDouble(500.0, 4000.0)
                )
                candles[candles.size - 1] = updatedCandle
            }

            // Calculate RSI & EMA
            val rsi = calculateRsi(candles.map { it.close })
            val ema = calculateEma(candles.map { it.close }, 9)

            val timing = when {
                rsi < 32 -> TimingSignal.STRONG_BUY
                rsi > 68 -> TimingSignal.STRONG_SELL
                spread < 0.0022 -> TimingSignal.ARBITRAGE_OPPORTUNITY
                else -> TimingSignal.NEUTRAL_WAIT
            }

            val parts = pair.split("/")
            val quote = CurrencyQuote(
                pair = pair,
                baseCurrency = parts.getOrNull(0) ?: "USD",
                targetCurrency = parts.getOrNull(1) ?: "BRL",
                bid = bid,
                ask = ask,
                spreadCentavos = spread * 100.0, // centavos
                dayHigh = candles.maxOfOrNull { it.high } ?: ask,
                dayLow = candles.minOfOrNull { it.low } ?: bid,
                changePercent = ((ask - (candles.firstOrNull()?.open ?: ask)) / (candles.firstOrNull()?.open ?: ask)) * 100.0,
                rsi14 = rsi,
                ema9 = ema,
                timingRecommendation = timing,
                historyCandles = candles.toList()
            )

            return Pair(newPrice, quote)
        }

        val (newUsdBrl, qUsdBrl) = updatePair("USD/BRL", usdBrl, 0.0040)
        usdBrl = newUsdBrl

        val (newEurBrl, qEurBrl) = updatePair("EUR/BRL", eurBrl, 0.0045)
        eurBrl = newEurBrl

        val (newGbpBrl, qGbpBrl) = updatePair("GBP/BRL", gbpBrl, 0.0060)
        gbpBrl = newGbpBrl

        val (newEurUsd, qEurUsd) = updatePair("EUR/USD", eurUsd, 0.0008)
        eurUsd = newEurUsd

        val (newGbpUsd, qGbpUsd) = updatePair("GBP/USD", gbpUsd, 0.0010)
        gbpUsd = newGbpUsd

        val (newUsdtUsd, qUsdtUsd) = updatePair("USDT/USD", usdtUsd, 0.0003)
        usdtUsd = newUsdtUsd

        _quotes.value = mapOf(
            "USD/BRL" to qUsdBrl,
            "EUR/BRL" to qEurBrl,
            "GBP/BRL" to qGbpBrl,
            "EUR/USD" to qEurUsd,
            "GBP/USD" to qGbpUsd,
            "USDT/USD" to qUsdtUsd
        )

        computeArbitrageOpportunities(qUsdBrl, qEurBrl, qEurUsd, qGbpBrl, qGbpUsd)
    }

    private fun computeArbitrageOpportunities(
        usdBrl: CurrencyQuote,
        eurBrl: CurrencyQuote,
        eurUsd: CurrencyQuote,
        gbpBrl: CurrencyQuote,
        gbpUsd: CurrencyQuote
    ) {
        val opps = mutableListOf<ArbitrageOpportunity>()

        // Triangular Route 1: BRL -> USD -> EUR -> BRL
        // 1000 BRL / usd_ask = X USD -> X * eurUsd.bid = Y EUR -> Y * eurBrl.bid = Z BRL
        val baseBrl = 1000.0
        val usdAmount = baseBrl / usdBrl.ask
        val eurAmount = usdAmount / eurUsd.ask
        val returnBrl = eurAmount * eurBrl.bid
        val profitBrl = returnBrl - baseBrl
        val profitCentavos = profitBrl * 100.0

        val wiseSpreadComp = (eurBrl.ask - (usdBrl.bid * eurUsd.bid)) * 100.0

        opps.add(
            ArbitrageOpportunity(
                id = "arb_triangular_1",
                routeName = "BRL ➔ USD ➔ EUR ➔ BRL",
                baseAmount = baseBrl,
                expectedReturn = returnBrl,
                profitCentavos = profitCentavos,
                spreadMarginPercent = (profitBrl / baseBrl) * 100.0,
                confidence = if (profitCentavos > 0) 92 else 74,
                wiseComparisonSpread = abs(wiseSpreadComp),
                recommendedAction = if (profitCentavos > 15.0) "EXECUTAR CONVERSÃO IMEDIATA" else "MONITORAR CENTAVOS DO CÂMBIO"
            )
        )

        // Route 2: USD -> EUR -> GBP -> USD
        val baseUsd = 500.0
        val eurFromUsd = baseUsd / eurUsd.ask
        val gbpFromEur = (eurFromUsd * eurBrl.bid) / gbpBrl.ask
        val usdReturn = gbpFromEur * gbpUsd.bid
        val usdProfit = usdReturn - baseUsd

        opps.add(
            ArbitrageOpportunity(
                id = "arb_triangular_2",
                routeName = "USD ➔ EUR ➔ GBP ➔ USD",
                baseAmount = baseUsd,
                expectedReturn = usdReturn,
                profitCentavos = usdProfit * 100.0,
                spreadMarginPercent = (usdProfit / baseUsd) * 100.0,
                confidence = 88,
                wiseComparisonSpread = abs(wiseSpreadComp * 0.8),
                recommendedAction = if (usdProfit > 0.05) "OPORTUNIDADE DE SPREAD ATIVA" else "AGUARDAR FLUTUAÇÃO DE CENTAVOS"
            )
        )

        // Route 3: Direct Wise vs Interbank Spread on USD/BRL
        val directSpreadCentavos = usdBrl.spreadCentavos
        opps.add(
            ArbitrageOpportunity(
                id = "arb_direct_wise",
                routeName = "Wise Direct Swap: BRL ➔ USD",
                baseAmount = 1000.0,
                expectedReturn = (1000.0 / usdBrl.bid),
                profitCentavos = directSpreadCentavos,
                spreadMarginPercent = (directSpreadCentavos / (usdBrl.bid * 100)) * 100.0,
                confidence = 95,
                wiseComparisonSpread = directSpreadCentavos,
                recommendedAction = if (usdBrl.timingRecommendation == TimingSignal.STRONG_BUY)
                    "COMPRAR DÓLAR AGORA (SPREAD BAIXO)"
                else
                    "AGUARDAR MELHOR CENTAVO DE COMPRA"
            )
        )

        _arbitrageOpportunities.value = opps
    }

    private fun calculateRsi(prices: List<Double>, period: Int = 14): Double {
        if (prices.size < 2) return 50.0
        var gains = 0.0
        var losses = 0.0
        val count = minOf(prices.size - 1, period)

        for (i in (prices.size - count) until prices.size) {
            val diff = prices[i] - prices[i - 1]
            if (diff >= 0) gains += diff else losses += abs(diff)
        }

        val avgGain = gains / count.toDouble()
        val avgLoss = losses / count.toDouble()

        if (avgLoss == 0.0) return 100.0
        val rs = avgGain / avgLoss
        return (100.0 - (100.0 / (1.0 + rs))).coerceIn(0.0, 100.0)
    }

    private fun calculateEma(prices: List<Double>, period: Int): Double {
        if (prices.isEmpty()) return 0.0
        val k = 2.0 / (period + 1.0)
        var ema = prices.first()
        for (i in 1 until prices.size) {
            ema = (prices[i] * k) + (ema * (1.0 - k))
        }
        return ema
    }
}
