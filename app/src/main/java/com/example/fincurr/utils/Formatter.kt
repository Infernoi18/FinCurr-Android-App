package com.example.fincurr.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

object Formatter {
    fun formatCurrency(amount: Double, currencyCode: String): String {
        val formatter = NumberFormat.getCurrencyInstance()
        formatter.currency = Currency.getInstance(currencyCode)
        return formatter.format(amount)
    }

    fun formatDate(timestamp: Long, pattern: String = "dd MMM, yyyy"): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))
    }
}
