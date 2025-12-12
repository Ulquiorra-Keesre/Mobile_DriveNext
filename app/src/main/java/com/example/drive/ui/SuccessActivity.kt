package com.example.drive.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drive.R
import com.example.drive.data.local.database.AppDatabase
import com.example.drive.data.model.Car
import com.example.drive.databinding.ActivitySuccessBinding
import com.example.drive.ui.Home.HomeActivity
import kotlinx.coroutines.launch
import java.util.*

class SuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuccessBinding
    private lateinit var btn_toHome: Button
    private lateinit var successText: TextView
    private lateinit var underSuccessText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        saveCarToDatabase() // Сохраняем автомобиль в базу
        setupClickListeners()
        setupBackButton()
    }

    private fun initViews() {
        btn_toHome = binding.btnToHome
        successText = binding.successText
        underSuccessText = binding.underSuccessText

        // Скрываем кнопку назад на этом экране
        binding.backButton.visibility = View.GONE
    }

    private fun saveCarToDatabase() {
        lifecycleScope.launch {
            try {
                // Получаем данные из Intent
                val address = intent.getStringExtra("CAR_ADDRESS") ?: ""
                val year = intent.getStringExtra("CAR_YEAR") ?: ""
                val brand = intent.getStringExtra("CAR_BRAND") ?: ""
                val model = intent.getStringExtra("CAR_MODEL") ?: ""
                val transmission = intent.getStringExtra("CAR_TRANSMISSION") ?: ""
                val mileage = intent.getStringExtra("CAR_MILEAGE")?.toIntOrNull() ?: 0
                val description = intent.getStringExtra("CAR_DESCRIPTION") ?: ""
                val firstPhotoUri = intent.getStringExtra("FIRST_PHOTO_URI") ?: ""

                // Создаем объект Car с ВСЕМИ обязательными полями
                val car = Car(
                    // id = 0 - теперь autoGenerate сработает
                    id = 0,

                    // Основные данные
                    brand = brand,
                    model = model,
                    year = year.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR),
                    transmission = transmission,
                    mileage = mileage,
                    description = description,
                    address = address,

                    // Обязательные поля с дефолтными значениями
                    pricePerDay = calculateDefaultPrice(year.toIntOrNull() ?: 2023, brand),
                    fuelType = "Бензин", // ← ДОБАВЬТЕ значение по умолчанию

                    // Фотографии
                    photoUrl = firstPhotoUri,
                    imageResId = 0, // ← ДОБАВЬТЕ дефолтное значение

                    // Статус
                    isAvailable = true
                )

                // Сохраняем в базу
                val database = AppDatabase.getDatabase(applicationContext)
                val newCarId = database.carDao().insertCar(car)

                runOnUiThread {
                    Toast.makeText(
                        this@SuccessActivity,
                        "✅ Автомобиль добавлен! ID: $newCarId",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("SuccessActivity", "Ошибка сохранения", e)
            }
        }
    }

    private fun calculateDefaultPrice(year: Int, brand: String): Double {
        // Простая логика расчета цены
        val basePrice = when {
            year >= 2020 -> 2500.0
            year >= 2015 -> 1800.0
            else -> 1200.0
        }

        val brandMultiplier = when (brand.lowercase()) {
            "bmw", "mercedes", "audi" -> 1.5
            "toyota", "honda", "volkswagen" -> 1.2
            else -> 1.0
        }

        return basePrice * brandMultiplier
    }

    private fun setupClickListeners() {
        btn_toHome.setOnClickListener {
            navigateToHome()
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            navigateToHome()
        }
    }


    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finishAffinity()
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateToHome()
        return true
    }
}