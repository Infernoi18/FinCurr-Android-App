package com.example.fincurr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val pinHash: String?,
    val createdAt: Long
)
