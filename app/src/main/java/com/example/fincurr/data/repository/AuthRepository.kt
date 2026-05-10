package com.example.fincurr.data.repository

import com.example.fincurr.data.local.dao.UserDao
import com.example.fincurr.data.model.UserEntity
import com.example.fincurr.utils.HashUtils
import com.example.fincurr.utils.PrefsManager

class AuthRepository(
    private val userDao: UserDao,
    private val prefs: PrefsManager
) {
    suspend fun getUser(): UserEntity? = userDao.getUser()

    suspend fun signup(fullName: String, email: String, password: String): Boolean {
        val user = UserEntity(
            id = 1,
            fullName = fullName,
            email = email,
            passwordHash = HashUtils.sha256(password),
            pinHash = null,
            createdAt = System.currentTimeMillis()
        )
        userDao.upsert(user)
        prefs.clearSession()
        return true
    }

    suspend fun login(email: String, password: String): UserEntity? {
        val user = userDao.getUser() ?: return null
        val matches = user.email.equals(email, true) && user.passwordHash == HashUtils.sha256(password)
        return if (matches) {
            prefs.isLoggedIn = false
            prefs.isPinVerified = false
            user
        } else null
    }

    suspend fun setPin(pin: String) {
        userDao.updatePin(HashUtils.sha256(pin))
    }

    suspend fun verifyPin(pin: String): Boolean {
        val user = userDao.getUser() ?: return false
        val hash = user.pinHash ?: return false
        return hash == HashUtils.sha256(pin)
    }

    fun completeLogin() {
        prefs.isLoggedIn = true
        prefs.isPinVerified = true
    }

    fun logout() {
        prefs.clearSession()
    }

    suspend fun updateProfile(fullName: String, email: String): Boolean {
        val user = userDao.getUser() ?: return false
        userDao.upsert(user.copy(fullName = fullName, email = email))
        return true
    }
}
