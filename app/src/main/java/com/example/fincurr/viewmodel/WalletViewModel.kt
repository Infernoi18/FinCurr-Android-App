package com.example.fincurr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincurr.data.model.TransactionEntity
import com.example.fincurr.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class WalletUiState(
    val balance: Double = 0.0,
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val message: String? = null,
    val error: String? = null
)

class WalletViewModel(private val repository: WalletRepository) : ViewModel() {
    private val _state = MutableStateFlow(WalletUiState())
    val state: StateFlow<WalletUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeWallet().collectLatest { wallet ->
                val balance = wallet?.balance ?: 0.0
                _state.value = _state.value.copy(balance = balance)
            }
        }
        viewModelScope.launch {
            repository.observeTransactions().collectLatest { list ->
                _state.value = _state.value.copy(recentTransactions = list.take(5))
            }
        }
    }

    fun addBalance(amount: Double, note: String) {
        viewModelScope.launch {
            val ok = repository.addBalance(amount, note)
            _state.value = _state.value.copy(
                message = if (ok) "Balance added" else null,
                error = if (!ok) "Enter a valid amount" else null
            )
        }
    }

    fun sendMoney(amount: Double, note: String) {
        viewModelScope.launch {
            val ok = repository.sendMoney(amount, note)
            _state.value = _state.value.copy(
                message = if (ok) "Transfer sent" else null,
                error = if (!ok) "Insufficient balance or invalid amount" else null
            )
        }
    }

    fun receiveMoney(amount: Double, note: String) {
        viewModelScope.launch {
            val ok = repository.receiveMoney(amount, note)
            _state.value = _state.value.copy(
                message = if (ok) "Money received" else null,
                error = if (!ok) "Enter a valid amount" else null
            )
        }
    }

    fun addExpense(amount: Double, note: String, category: String?) {
        viewModelScope.launch {
            val ok = repository.addExpense(amount, note, category)
            _state.value = _state.value.copy(
                message = if (ok) "Expense saved" else null,
                error = if (!ok) "Insufficient balance or invalid amount" else null
            )
        }
    }
}
