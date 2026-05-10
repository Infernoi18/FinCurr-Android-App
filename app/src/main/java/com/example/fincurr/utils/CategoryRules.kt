package com.example.fincurr.utils

object CategoryRules {
    private val rules = mapOf(
        "Food" to listOf("food", "cafe", "restaurant", "dine", "pizza", "coffee"),
        "Travel" to listOf("uber", "ola", "bus", "train", "flight", "taxi", "cab"),
        "Groceries" to listOf("grocery", "supermarket", "mart", "store"),
        "Shopping" to listOf("amazon", "flipkart", "mall", "shopping"),
        "Bills" to listOf("electric", "water", "wifi", "recharge", "bill"),
        "Health" to listOf("pharmacy", "doctor", "clinic", "hospital", "medicine"),
        "Entertainment" to listOf("movie", "netflix", "spotify", "game")
    )

    fun categorize(note: String): String {
        val lower = note.lowercase()
        for ((category, keywords) in rules) {
            if (keywords.any { lower.contains(it) }) {
                return category
            }
        }
        return "Others"
    }
}
