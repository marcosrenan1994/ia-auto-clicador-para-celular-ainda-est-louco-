package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screen_memories")
data class ScreenMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val packageName: String,
    val windowTitle: String,
    val extractedSummary: String,
    val detectedCurrencies: String, // e.g. "USD, BRL, EUR"
    val detectedRates: String,      // e.g. "USD/BRL: 5.624, EUR/BRL: 6.082"
    val clickableElementsCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val pair: String, // "USD/BRL", "EUR/BRL", "GBP/USD", "EUR/USD", etc.
    val bid: Double,
    val ask: Double,
    val spreadCentavos: Double, // (ask - bid) in cents
    val high24h: Double,
    val low24h: Double,
    val change24hPercent: Double,
    val rsiIndicator: Double,
    val recommendation: String, // "COMPRA FORTE", "VENDA FORTE", "AGUARDAR SPREAD"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "arbitrage_signals")
data class ArbitrageSignalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val basePair: String,
    val route: String, // e.g. "BRL -> USD -> EUR -> BRL"
    val direction: String, // "COMPRAR" / "VENDER"
    val currentRate: Double,
    val targetRate: Double,
    val estimatedProfitCentavos: Double,
    val confidencePercent: Int,
    val timingMoment: String, // "MOMENTO IDEAL (Centavos Favoráveis)", "AGUARDAR QUEDA"
    val executed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "autonomous_actions")
data class AutonomousActionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val actionType: String, // "MOVE_MOUSE", "CLICK_TARGET", "SCAN_SCREEN", "EXECUTE_ARBITRAGE"
    val startX: Int,
    val startY: Int,
    val targetX: Int,
    val targetY: Int,
    val targetLabel: String,
    val reason: String,
    val successful: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
