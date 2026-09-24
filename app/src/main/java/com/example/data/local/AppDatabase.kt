package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ArbitrageSignalDao
import com.example.data.local.dao.AutonomousActionDao
import com.example.data.local.dao.CurrencyRateDao
import com.example.data.local.dao.ScreenMemoryDao
import com.example.data.local.entity.ArbitrageSignalEntity
import com.example.data.local.entity.AutonomousActionEntity
import com.example.data.local.entity.CurrencyRateEntity
import com.example.data.local.entity.ScreenMemoryEntity

@Database(
    entities = [
        ScreenMemoryEntity::class,
        CurrencyRateEntity::class,
        ArbitrageSignalEntity::class,
        AutonomousActionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun screenMemoryDao(): ScreenMemoryDao
    abstract fun currencyRateDao(): CurrencyRateDao
    abstract fun arbitrageSignalDao(): ArbitrageSignalDao
    abstract fun autonomousActionDao(): AutonomousActionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "iaut_clic_master.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
