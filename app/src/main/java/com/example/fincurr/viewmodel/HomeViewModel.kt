package com.example.fincurr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincurr.data.model.TransactionEntity
import com.example.fincurr.data.model.TransactionType
import com.example.fincurr.data.repository.TransactionRepository
import com.example.fincurr.data.repository.WalletRepository
import com.example.fincurr.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HomeUiState(
    val balance: Double = 0.0,
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val monthlySpend: Double = 0.0
)

class HomeViewModel(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            walletRepository.observeWallet().collectLatest { wallet ->
                _state.value = _state.value.copy(balance = wallet?.balance ?: 0.0)
            }
        }
        viewModelScope.launch {
            transactionRepository.observeTransactions().collectLatest { list ->
                val range = DateUtils.monthRange(DateUtils.currentMonthYear())
                val monthlySpend = list.filter {
                    it.type == TransactionType.DEBIT && it.timestamp in range.first..range.second
                }.sumOf { it.amount }
                _state.value = _state.value.copy(
                    recentTransactions = list.take(6),
                    monthlySpend = monthlySpend
                )
            }
        }
    }
}
