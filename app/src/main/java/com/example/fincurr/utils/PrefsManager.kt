package com.example.fincurr.utils

import android.content.Context

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("fincurr_prefs", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean("is_logged_in", false)
        set(value) = prefs.edit().putBoolean("is_logged_in", value).apply()

    var isPinVerified: Boolean
        get() = prefs.getBoolean("is_pin_verified", false)
        set(value) = prefs.edit().putBoolean("is_pin_verified", value).apply()

    var currency: String
        get() = prefs.getString("currency", "INR") ?: "INR"
        set(value) = prefs.edit().putString("currency", value).apply()

    var darkMode: Boolean
        get() = prefs.getBoolean("dark_mode", false)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean("notifications_enabled", true)
        set(value) = prefs.edit().putBoolean("notifications_enabled", value).apply()

    fun clearSession() {
        isLoggedIn = false
        isPinVerified = false
    }
}
