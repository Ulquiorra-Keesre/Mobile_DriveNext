package com.example.drive.data.local.dao

import androidx.room.*
import com.example.drive.data.model.Car
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {

    @Query("SELECT * FROM cars ORDER BY brand, model")
    fun getAllCars(): Flow<List<Car>>

    @Query("SELECT * FROM cars WHERE isAvailable = 1 ORDER BY pricePerDay ASC")
    fun getAvailableCars(): Flow<List<Car>>

    @Query("SELECT * FROM cars WHERE id = :carId")
    suspend fun getCarById(carId: Int): Car?

    @Query("SELECT * FROM cars WHERE brand LIKE '%' || :query || '%' OR model LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchCars(query: String): Flow<List<Car>>

    // Изменяем этот метод, чтобы он возвращал ID вставленной записи
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: Car): Long // Добавляем возврат Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCars(cars: List<Car>)

    @Update
    suspend fun updateCar(car: Car)

    @Query("UPDATE cars SET isAvailable = :isAvailable WHERE id = :carId")
    suspend fun updateCarAvailability(carId: Int, isAvailable: Boolean)

    @Query("DELETE FROM cars")
    suspend fun deleteAllCars()
}