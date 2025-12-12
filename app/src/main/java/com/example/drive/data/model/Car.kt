package com.example.drive.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey(autoGenerate = true) // ← ДОБАВЬТЕ ЭТО
    val id: Int = 0, // Теперь можно оставить 0

    val brand: String,
    val model: String,
    val pricePerDay: Double,
    val year: Int,
    val fuelType: String,           // "Бензин", "Дизель", "Электро", "Гибрид"
    val transmission: String,       // "Автомат", "Механика", "Робот", "Вариатор"
    val description: String = "",
    val mileage: Int,
    val isAvailable: Boolean = true,
    val imageResId: Int = 0,
    val photoUrl: String = "",
    val address: String
) {

    fun getFullName(): String = "$brand \n $model"

    fun getPriceFormatted(): String = "%,.0f₽ в день".format(pricePerDay)

    fun getSpecs(): String = "$transmission • $fuelType"

    fun getYearFormatted(): String = "$year г."
}