package com.example.fincurr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincurr.data.model.TransactionEntity
import com.example.fincurr.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TransactionDetailState(
    val transaction: TransactionEntity? = null
)

class TransactionDetailViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val _state = MutableStateFlow(TransactionDetailState())
    val state: StateFlow<TransactionDetailState> = _state.asStateFlow()

    fun load(transactionId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(transaction = repository.getById(transactionId))
        }
    }
}
