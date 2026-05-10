package com.example.fincurr.data.repository

import com.example.fincurr.data.local.dao.TransactionDao
import com.example.fincurr.data.model.TransactionEntity

class InsightsRepository(private val transactionDao: TransactionDao) {
    suspend fun getBetween(start: Long, end: Long): List<TransactionEntity> {
        return transactionDao.getBetween(start, end)
    }
}
