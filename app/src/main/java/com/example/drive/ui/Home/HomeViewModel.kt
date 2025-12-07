package com.example.drive.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drive.data.model.Car
import com.example.drive.data.repository.CarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: CarRepository
) : ViewModel() {

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCars()
    }

    private fun loadCars() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.getAllCars().collectLatest { carList ->
                    _cars.value = carList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки данных"
                _isLoading.value = false
            }
        }
    }

    fun searchCars(query: String) {
        viewModelScope.launch {
            repository.searchCars(query).collectLatest { results ->
                _cars.value = results
            }
        }
    }

    suspend fun bookCar(carId: Int): Boolean {
        return try {
            repository.bookCar(carId)
        } catch (e: Exception) {
            false
        }
    }
}