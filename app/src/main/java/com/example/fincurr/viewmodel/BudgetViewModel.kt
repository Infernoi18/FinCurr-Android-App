package com.example.fincurr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincurr.data.model.TransactionType
import com.example.fincurr.data.repository.BudgetRepository
import com.example.fincurr.data.repository.TransactionRepository
import com.example.fincurr.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class BudgetUiState(
    val budget: Double = 0.0,
    val spent: Double = 0.0
)

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(BudgetUiState())
    val state: StateFlow<BudgetUiState> = _state.asStateFlow()

    private val currentMonth = DateUtils.currentMonthYear()

    init {
        viewModelScope.launch {
            budgetRepository.observeBudget(currentMonth).collectLatest { budget ->
                _state.value = _state.value.copy(budget = budget?.amount ?: 0.0)
            }
        }
        viewModelScope.launch {
            transactionRepository.observeTransactions().collectLatest { list ->
                val range = DateUtils.monthRange(currentMonth)
                val spent = list.filter {
                    it.type == TransactionType.DEBIT && it.timestamp in range.first..range.second
                }.sumOf { it.amount }
                _state.value = _state.value.copy(spent = spent)
            }
        }
    }

    fun setBudget(amount: Double) {
        viewModelScope.launch {
            budgetRepository.setBudget(currentMonth, amount)
        }
    }

    fun refreshSpent() {
        viewModelScope.launch {
            val range = DateUtils.monthRange(currentMonth)
            val tx = transactionRepository.getBetween(range.first, range.second)
            val spent = tx.filter { it.type == TransactionType.DEBIT }.sumOf { it.amount }
            _state.value = _state.value.copy(spent = spent)
        }
    }
}
