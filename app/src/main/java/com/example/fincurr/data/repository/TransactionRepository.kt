package com.example.fincurr.data.repository

import com.example.fincurr.data.local.dao.TransactionDao
import com.example.fincurr.data.model.TransactionEntity
import com.example.fincurr.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    fun observeTransactions(): Flow<List<TransactionEntity>> = transactionDao.observeAll()

    fun searchTransactions(query: String): Flow<List<TransactionEntity>> = transactionDao.search(query)

    fun observeByType(type: TransactionType): Flow<List<TransactionEntity>> = transactionDao.observeByType(type)

    fun observeByCategory(category: String): Flow<List<TransactionEntity>> = transactionDao.observeByCategory(category)

    suspend fun getById(id: Long): TransactionEntity? = transactionDao.getById(id)

    suspend fun getBetween(start: Long, end: Long): List<TransactionEntity> = transactionDao.getBetween(start, end)
}
