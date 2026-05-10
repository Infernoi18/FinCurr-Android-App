package com.example.fincurr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincurr.data.model.UserEntity
import com.example.fincurr.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: UserEntity? = null,
    val message: String? = null
)

class ProfileViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(user = repository.getUser())
        }
    }

    fun updateProfile(fullName: String, email: String) {
        viewModelScope.launch {
            val ok = repository.updateProfile(fullName, email)
            _state.value = _state.value.copy(
                user = repository.getUser(),
                message = if (ok) "Profile updated" else "Update failed"
            )
        }
    }
}
