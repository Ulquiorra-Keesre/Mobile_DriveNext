package com.example.drive.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R
import com.example.drive.databinding.ActivityAddCar1Binding
import com.example.drive.ui.AddCar2Activity

class AddCar1Activity: AppCompatActivity() {

    private lateinit var binding: ActivityAddCar1Binding

    private lateinit var btn_toAddCar2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCar1Binding.inflate(layoutInflater)
        setContentView(binding.root)



        initViews()
        setupHeader()
        setupClickListeners()
    }

    private fun initViews() {
        btn_toAddCar2 = findViewById(R.id.btn_toAddCar2)
    }

    private fun setupClickListeners() {
        // Кнопка переход на экран добавления автомобиля
        btn_toAddCar2.setOnClickListener {
            val intent = Intent(this, AddCar2Activity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupHeader() {
        // Настройка кнопки назад
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }
}