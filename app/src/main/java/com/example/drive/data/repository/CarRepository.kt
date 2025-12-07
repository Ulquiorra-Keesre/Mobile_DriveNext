package com.example.drive.data.repository

import com.example.drive.data.local.dao.CarDao
import com.example.drive.data.model.Car
import kotlinx.coroutines.flow.Flow

class CarRepository(
    private val carDao: CarDao  // Просто передаем через конструктор
) {

    fun getAllCars(): Flow<List<Car>> = carDao.getAllCars()

    fun getAvailableCars(): Flow<List<Car>> = carDao.getAvailableCars()

    fun searchCars(query: String): Flow<List<Car>> {
        return if (query.isBlank()) {
            carDao.getAllCars()
        } else {
            carDao.searchCars(query)
        }
    }

    suspend fun getCarById(carId: Int): Car? = carDao.getCarById(carId)

    suspend fun bookCar(carId: Int): Boolean {
        val car = carDao.getCarById(carId)
        car?.let {
            if (it.isAvailable) {
                carDao.updateCar(it.copy(isAvailable = false))
                return true
            }
        }
        return false
    }

    suspend fun cancelBooking(carId: Int) {
        val car = carDao.getCarById(carId)
        car?.let {
            carDao.updateCar(it.copy(isAvailable = true))
        }
    }
}