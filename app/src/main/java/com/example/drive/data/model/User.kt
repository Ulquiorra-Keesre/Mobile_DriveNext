package com.example.drive.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int = 1,
    val name: String = "Иван Иванов",
    val email: String = "user@example.com",
    val phone: String = "+7 (999) 123-45-67",
    val avatarResId: Int = 0,  // Будет R.drawable.avatar_default
    val driverLicense: String? = null,
    val address: String? = null
)