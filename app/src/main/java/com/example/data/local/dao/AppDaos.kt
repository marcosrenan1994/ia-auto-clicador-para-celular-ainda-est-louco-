package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ArbitrageSignalEntity
import com.example.data.local.entity.AutonomousActionEntity
import com.example.data.local.entity.CurrencyRateEntity
import com.example.data.local.entity.ScreenMemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenMemoryDao {
    @Query("SELECT * FROM screen_memories ORDER BY timestamp DESC LIMIT 50")
    fun getRecentMemories(): Flow<List<ScreenMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: ScreenMemoryEntity): Long

    @Query("DELETE FROM screen_memories")
    suspend fun clearMemories()
}

@Dao
interface CurrencyRateDao {
    @Query("SELECT * FROM currency_rates ORDER BY timestamp DESC LIMIT 100")
    fun getAllRates(): Flow<List<CurrencyRateEntity>>

    @Query("SELECT * FROM currency_rates WHERE pair = :pair ORDER BY timestamp DESC LIMIT 60")
    fun getRatesForPair(pair: String): Flow<List<CurrencyRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rate: CurrencyRateEntity): Long

    @Query("DELETE FROM currency_rates WHERE timestamp < :olderThanMs")
    suspend fun purgeOldRates(olderThanMs: Long)
}

@Dao
interface ArbitrageSignalDao {
    @Query("SELECT * FROM arbitrage_signals ORDER BY timestamp DESC LIMIT 50")
    fun getSignals(): Flow<List<ArbitrageSignalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: ArbitrageSignalEntity): Long

    @Query("UPDATE arbitrage_signals SET executed = 1 WHERE id = :id")
    suspend fun markExecuted(id: Long)

    @Query("DELETE FROM arbitrage_signals")
    suspend fun clearSignals()
}

@Dao
interface AutonomousActionDao {
    @Query("SELECT * FROM autonomous_actions ORDER BY timestamp DESC LIMIT 100")
    fun getRecentActions(): Flow<List<AutonomousActionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: AutonomousActionEntity): Long

    @Query("DELETE FROM autonomous_actions")
    suspend fun clearActions()
}
