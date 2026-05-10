package com.example.fincurr.data.repository

import com.example.fincurr.data.local.dao.BudgetDao
import com.example.fincurr.data.model.BudgetEntity
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {
    fun observeBudget(monthYear: String): Flow<BudgetEntity?> = budgetDao.observeBudget(monthYear)

    suspend fun setBudget(monthYear: String, amount: Double) {
        budgetDao.upsert(BudgetEntity(monthYear = monthYear, amount = amount))
    }
}
