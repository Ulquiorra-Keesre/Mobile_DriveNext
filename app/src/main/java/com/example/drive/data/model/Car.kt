package com.example.drive.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey val id: Int,
    val brand: String,
    val model: String,
    val pricePerDay: Double,
    val year: Int,
    val fuelType: String,           // "Бензин", "Дизель", "Электро", "Гибрид"
    val transmission: String,       // "Автомат", "Механика", "Робот", "Вариатор"
    val seats: Int,
    val engineVolume: String? = null,
    val power: Int? = null,
    val color: String? = null,
    val imageResId: Int = 0,
    val isAvailable: Boolean = true,
    val rating: Float = 4.5f,
    val description: String = ""
) {

    fun getFullName(): String = "$brand $model"

    fun getPriceFormatted(): String = "%,.0f₽ в день".format(pricePerDay)

    fun getSpecs(): String = "$fuelType • $transmission • $seats мест"

    fun getYearFormatted(): String = "$year г."
}