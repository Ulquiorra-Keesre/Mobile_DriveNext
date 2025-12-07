package com.example.drive.data.local.dao

import androidx.room.*
import com.example.drive.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE id = 1")
    fun getCurrentUser(): Flow<User?>

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET avatarResId = :avatarResId WHERE id = 1")
    suspend fun updateUserAvatar(avatarResId: Int)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}