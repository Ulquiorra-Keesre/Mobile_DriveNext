package com.example.drive

import android.app.Application
import com.example.drive.data.local.database.AppDatabase
import com.example.drive.data.repository.CarRepository
import com.example.drive.data.repository.UserRepository

class DriveApp : Application() {

    // База данных Room
    val database by lazy { AppDatabase.getDatabase(this) }

    // Repository (создаем при первом обращении)
    val carRepository by lazy {
        CarRepository(database.carDao())
    }

    val userRepository by lazy {
        UserRepository(database.userDao())
    }

    override fun onCreate() {
        super.onCreate()
        // Дополнительная инициализация не требуется
    }
}