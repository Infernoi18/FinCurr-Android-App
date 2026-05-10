package com.example.fincurr.di

import android.content.Context
import com.example.fincurr.data.local.AppDatabase
import com.example.fincurr.data.repository.AuthRepository
import com.example.fincurr.data.repository.BudgetRepository
import com.example.fincurr.data.repository.InsightsRepository
import com.example.fincurr.data.repository.TransactionRepository
import com.example.fincurr.data.repository.WalletRepository
import com.example.fincurr.utils.PrefsManager

class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    val prefs = PrefsManager(context)
    val authRepository = AuthRepository(database.userDao(), prefs)
    val walletRepository = WalletRepository(database.walletDao(), database.transactionDao())
    val transactionRepository = TransactionRepository(database.transactionDao())
    val budgetRepository = BudgetRepository(database.budgetDao())
    val insightsRepository = InsightsRepository(database.transactionDao())
}
