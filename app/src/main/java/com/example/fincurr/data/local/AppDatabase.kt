package com.example.fincurr.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fincurr.data.local.dao.BudgetDao
import com.example.fincurr.data.local.dao.TransactionDao
import com.example.fincurr.data.local.dao.UserDao
import com.example.fincurr.data.local.dao.WalletDao
import com.example.fincurr.data.model.BudgetEntity
import com.example.fincurr.data.model.TransactionEntity
import com.example.fincurr.data.model.UserEntity
import com.example.fincurr.data.model.WalletEntity

@Database(
    entities = [UserEntity::class, WalletEntity::class, TransactionEntity::class, BudgetEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fincurr.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
