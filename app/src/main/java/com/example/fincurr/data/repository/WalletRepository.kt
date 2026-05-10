package com.example.fincurr.data.repository

import com.example.fincurr.data.local.dao.TransactionDao
import com.example.fincurr.data.local.dao.WalletDao
import com.example.fincurr.data.model.TransactionEntity
import com.example.fincurr.data.model.TransactionType
import com.example.fincurr.data.model.WalletEntity
import com.example.fincurr.utils.CategoryRules
import kotlinx.coroutines.flow.Flow

class WalletRepository(
    private val walletDao: WalletDao,
    private val transactionDao: TransactionDao
) {
    fun observeWallet(): Flow<WalletEntity?> = walletDao.observeWallet()

    fun observeTransactions(): Flow<List<TransactionEntity>> = transactionDao.observeAll()

    private suspend fun ensureWallet() {
        if (walletDao.getWallet() == null) {
            walletDao.upsert(WalletEntity(id = 1, balance = 0.0))
        }
    }

    suspend fun addBalance(amount: Double, note: String): Boolean {
        if (amount <= 0) return false
        ensureWallet()
        val wallet = walletDao.getWallet() ?: return false
        val newBalance = wallet.balance + amount
        walletDao.updateBalance(newBalance)
        transactionDao.insert(
            TransactionEntity(
                amount = amount,
                type = TransactionType.CREDIT,
                category = "Top Up",
                note = note.ifBlank { "Wallet top up" },
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun sendMoney(amount: Double, note: String, category: String = "Transfer"): Boolean {
        if (amount <= 0) return false
        ensureWallet()
        val wallet = walletDao.getWallet() ?: return false
        if (wallet.balance < amount) return false
        val newBalance = wallet.balance - amount
        walletDao.updateBalance(newBalance)
        transactionDao.insert(
            TransactionEntity(
                amount = amount,
                type = TransactionType.DEBIT,
                category = category,
                note = note.ifBlank { "Sent money" },
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun receiveMoney(amount: Double, note: String): Boolean {
        if (amount <= 0) return false
        ensureWallet()
        val wallet = walletDao.getWallet() ?: return false
        val newBalance = wallet.balance + amount
        walletDao.updateBalance(newBalance)
        transactionDao.insert(
            TransactionEntity(
                amount = amount,
                type = TransactionType.CREDIT,
                category = "Transfer In",
                note = note.ifBlank { "Received money" },
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun addExpense(amount: Double, note: String, manualCategory: String?): Boolean {
        if (amount <= 0) return false
        ensureWallet()
        val wallet = walletDao.getWallet() ?: return false
        if (wallet.balance < amount) return false
        val newBalance = wallet.balance - amount
        walletDao.updateBalance(newBalance)
        val category = manualCategory?.ifBlank { null } ?: CategoryRules.categorize(note)
        transactionDao.insert(
            TransactionEntity(
                amount = amount,
                type = TransactionType.DEBIT,
                category = category,
                note = note.ifBlank { "Expense" },
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }
}
