package com.example.fincurr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincurr.data.model.TransactionType
import com.example.fincurr.data.repository.InsightsRepository
import com.example.fincurr.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class InsightsUiState(
    val categoryTotals: Map<String, Double> = emptyMap(),
    val weeklyTotals: List<Double> = emptyList(),
    val monthSpend: Double = 0.0,
    val monthIncome: Double = 0.0,
    val previousMonthSpend: Double = 0.0
)

class InsightsViewModel(private val repository: InsightsRepository) : ViewModel() {
    private val _state = MutableStateFlow(InsightsUiState())
    val state: StateFlow<InsightsUiState> = _state.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val currentRange = DateUtils.monthRange(DateUtils.currentMonthYear())
            val currentTx = repository.getBetween(currentRange.first, currentRange.second)
            val categoryTotals = currentTx.filter { it.type == TransactionType.DEBIT }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
            val monthSpend = currentTx.filter { it.type == TransactionType.DEBIT }.sumOf { it.amount }
            val monthIncome = currentTx.filter { it.type == TransactionType.CREDIT }.sumOf { it.amount }

            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -1)
            val prevMonthYear = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault()).format(cal.time)
            val prevRange = DateUtils.monthRange(prevMonthYear)
            val prevTx = repository.getBetween(prevRange.first, prevRange.second)
            val prevSpend = prevTx.filter { it.type == TransactionType.DEBIT }.sumOf { it.amount }

            val weeklyTotals = mutableListOf<Double>()
            val weekCal = Calendar.getInstance()
            weekCal.set(Calendar.HOUR_OF_DAY, 0)
            weekCal.set(Calendar.MINUTE, 0)
            weekCal.set(Calendar.SECOND, 0)
            weekCal.set(Calendar.MILLISECOND, 0)
            for (i in 0 until 4) {
                val end = weekCal.timeInMillis
                weekCal.add(Calendar.DAY_OF_YEAR, -7)
                val start = weekCal.timeInMillis
                val weekTx = repository.getBetween(start, end)
                val total = weekTx.filter { it.type == TransactionType.DEBIT }.sumOf { it.amount }
                weeklyTotals.add(total)
            }

            _state.value = InsightsUiState(
                categoryTotals = categoryTotals,
                weeklyTotals = weeklyTotals.reversed(),
                monthSpend = monthSpend,
                monthIncome = monthIncome,
                previousMonthSpend = prevSpend
            )
        }
    }
}
