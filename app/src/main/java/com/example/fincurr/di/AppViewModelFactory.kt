package com.example.fincurr.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fincurr.FincurrApp
import com.example.fincurr.viewmodel.AuthViewModel
import com.example.fincurr.viewmodel.BudgetViewModel
import com.example.fincurr.viewmodel.HomeViewModel
import com.example.fincurr.viewmodel.InsightsViewModel
import com.example.fincurr.viewmodel.ProfileViewModel
import com.example.fincurr.viewmodel.SettingsViewModel
import com.example.fincurr.viewmodel.TransactionDetailViewModel
import com.example.fincurr.viewmodel.TransactionsViewModel
import com.example.fincurr.viewmodel.WalletViewModel

class AppViewModelFactory(private val app: FincurrApp) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val container = app.container
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(container.authRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(container.walletRepository, container.transactionRepository) as T
            modelClass.isAssignableFrom(WalletViewModel::class.java) ->
                WalletViewModel(container.walletRepository) as T
            modelClass.isAssignableFrom(TransactionsViewModel::class.java) ->
                TransactionsViewModel(container.transactionRepository) as T
            modelClass.isAssignableFrom(TransactionDetailViewModel::class.java) ->
                TransactionDetailViewModel(container.transactionRepository) as T
            modelClass.isAssignableFrom(InsightsViewModel::class.java) ->
                InsightsViewModel(container.insightsRepository) as T
            modelClass.isAssignableFrom(BudgetViewModel::class.java) ->
                BudgetViewModel(container.budgetRepository, container.transactionRepository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(container.authRepository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(container.prefs) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
