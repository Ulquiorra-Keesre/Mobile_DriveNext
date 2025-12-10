package com.example.drive.ui.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drive.DriveApp

class HomeViewModelFactory(
    private val application: DriveApp
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(application.carRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}