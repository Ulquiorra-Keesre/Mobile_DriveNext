package com.example.drive.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val carId: Int,
    val userId: Int = 1,
    val startDate: Long,
    val endDate: Long,
    val totalPrice: Double,
    val status: String = "active",  // "active", "completed", "cancelled"
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getDurationDays(): Int {
        val diff = endDate - startDate
        return (diff / (1000 * 60 * 60 * 24)).toInt() + 1
    }

    fun getFormattedDateRange(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return "${sdf.format(Date(startDate))} - ${sdf.format(Date(endDate))}"
    }
}