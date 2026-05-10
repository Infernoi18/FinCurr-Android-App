package com.example.fincurr.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fincurr.utils.PrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val currency: String = "INR"
)

class SettingsViewModel(private val prefs: PrefsManager) : ViewModel() {
    private val _state = MutableStateFlow(
        SettingsUiState(
            darkMode = prefs.darkMode,
            notificationsEnabled = prefs.notificationsEnabled,
            currency = prefs.currency
        )
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        prefs.darkMode = enabled
        _state.value = _state.value.copy(darkMode = enabled)
    }

    fun setNotifications(enabled: Boolean) {
        prefs.notificationsEnabled = enabled
        _state.value = _state.value.copy(notificationsEnabled = enabled)
    }

    fun setCurrency(currency: String) {
        prefs.currency = currency
        _state.value = _state.value.copy(currency = currency)
    }
}
