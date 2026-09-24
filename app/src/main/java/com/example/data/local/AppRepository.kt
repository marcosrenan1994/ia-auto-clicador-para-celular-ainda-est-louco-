package com.example.data.local

import com.example.data.local.entity.ArbitrageSignalEntity
import com.example.data.local.entity.AutonomousActionEntity
import com.example.data.local.entity.CurrencyRateEntity
import com.example.data.local.entity.ScreenMemoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppRepository(private val database: AppDatabase) {

    val screenMemories: Flow<List<ScreenMemoryEntity>> =
        database.screenMemoryDao().getRecentMemories()

    val currencyRates: Flow<List<CurrencyRateEntity>> =
        database.currencyRateDao().getAllRates()

    val arbitrageSignals: Flow<List<ArbitrageSignalEntity>> =
        database.arbitrageSignalDao().getSignals()

    val autonomousActions: Flow<List<AutonomousActionEntity>> =
        database.autonomousActionDao().getRecentActions()

    suspend fun saveScreenMemory(memory: ScreenMemoryEntity): Long = withContext(Dispatchers.IO) {
        database.screenMemoryDao().insertMemory(memory)
    }

    suspend fun clearScreenMemories() = withContext(Dispatchers.IO) {
        database.screenMemoryDao().clearMemories()
    }

    suspend fun saveCurrencyRate(rate: CurrencyRateEntity): Long = withContext(Dispatchers.IO) {
        database.currencyRateDao().insertRate(rate)
    }

    suspend fun saveArbitrageSignal(signal: ArbitrageSignalEntity): Long = withContext(Dispatchers.IO) {
        database.arbitrageSignalDao().insertSignal(signal)
    }

    suspend fun markSignalExecuted(id: Long) = withContext(Dispatchers.IO) {
        database.arbitrageSignalDao().markExecuted(id)
    }

    suspend fun clearSignals() = withContext(Dispatchers.IO) {
        database.arbitrageSignalDao().clearSignals()
    }

    suspend fun recordAutonomousAction(action: AutonomousActionEntity): Long = withContext(Dispatchers.IO) {
        database.autonomousActionDao().insertAction(action)
    }

    suspend fun clearAutonomousActions() = withContext(Dispatchers.IO) {
        database.autonomousActionDao().clearActions()
    }
}
