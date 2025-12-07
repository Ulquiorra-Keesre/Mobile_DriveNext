package com.example.drive.data.repository

import com.example.drive.data.local.dao.UserDao
import com.example.drive.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao  // Просто передаем через конструктор
) {

    fun getCurrentUser(): Flow<User?> = userDao.getCurrentUser()

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun updateAvatar(avatarResId: Int) = userDao.updateUserAvatar(avatarResId)
}