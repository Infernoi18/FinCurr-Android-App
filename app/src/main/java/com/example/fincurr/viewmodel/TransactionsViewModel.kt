package com.example.fincurr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincurr.data.model.TransactionEntity
import com.example.fincurr.data.model.TransactionType
import com.example.fincurr.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class TransactionFilter(
    val type: TransactionType? = null,
    val category: String? = null,
    val dateRange: Pair<Long, Long>? = null
)

data class TransactionsUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val query: String = "",
    val filter: TransactionFilter = TransactionFilter()
)

class TransactionsViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val _state = MutableStateFlow(TransactionsUiState())
    val state: StateFlow<TransactionsUiState> = _state.asStateFlow()
    private var allTransactions: List<TransactionEntity> = emptyList()

    init {
        viewModelScope.launch {
            repository.observeTransactions().collectLatest { list ->
                allTransactions = list
                applyFilters()
            }
        }
    }

    fun updateQuery(query: String) {
        _state.value = _state.value.copy(query = query)
        applyFilters()
    }

    fun setFilter(filter: TransactionFilter) {
        _state.value = _state.value.copy(filter = filter)
        applyFilters()
    }

    private fun applyFilters() {
        val query = _state.value.query
        val filter = _state.value.filter
        var list = allTransactions
        if (query.isNotBlank()) {
            val lower = query.lowercase()
            list = list.filter {
                it.note.lowercase().contains(lower) || it.category.lowercase().contains(lower)
            }
        }
        filter.type?.let { type ->
            list = list.filter { it.type == type }
        }
        filter.category?.let { category ->
            list = list.filter { it.category.equals(category, true) }
        }
        filter.dateRange?.let { range ->
            list = list.filter { it.timestamp in range.first..range.second }
        }
        _state.value = _state.value.copy(transactions = list)
    }
}
