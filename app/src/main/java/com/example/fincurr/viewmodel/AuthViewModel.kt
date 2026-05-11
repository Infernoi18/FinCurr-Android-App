package com.example.fincurr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincurr.data.model.UserEntity
import com.example.fincurr.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val loading: Boolean = false,
    val error: String? = null,
    val user: UserEntity? = null,
    val signedUp: Boolean = false,
    val loginSucceeded: Boolean = false,
    val pinSet: Boolean = false,
    val pinVerified: Boolean = false
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun signup(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null, signedUp = false)
            val success = repository.signup(fullName, email, password)
            _state.value = _state.value.copy(loading = false, signedUp = success, user = repository.getUser())
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                loading = true,
                error = null,
                user = null,
                loginSucceeded = false,
                pinSet = false
            )
            val user = repository.login(email, password)
            if (user == null) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = "Invalid credentials",
                    loginSucceeded = false
                )
            } else {
                _state.value = _state.value.copy(
                    loading = false,
                    user = user,
                    loginSucceeded = true,
                    pinSet = !user.pinHash.isNullOrBlank()
                )
            }
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            val user = repository.getUser()
            _state.value = _state.value.copy(
                user = user,
                pinSet = !user?.pinHash.isNullOrBlank(),
                loginSucceeded = false
            )
        }
    }

    fun setPin(pin: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            repository.setPin(pin)
            _state.value = _state.value.copy(loading = false, pinSet = true, user = repository.getUser())
        }
    }

    fun verifyPin(pin: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            val verified = repository.verifyPin(pin)
            if (verified) {
                repository.completeLogin()
                _state.value = _state.value.copy(loading = false, pinVerified = true)
            } else {
                _state.value = _state.value.copy(loading = false, error = "Incorrect PIN")
            }
        }
    }

    fun logout() {
        repository.logout()
    }

    fun completeLogin() {
        repository.completeLogin()
        _state.value = _state.value.copy(pinVerified = true)
    }
}
