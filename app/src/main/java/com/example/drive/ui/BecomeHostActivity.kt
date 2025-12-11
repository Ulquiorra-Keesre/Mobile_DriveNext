package com.example.drive.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R
import com.example.drive.ui.AddCarActivity

class BecomeHostActivity : AppCompatActivity() {

    private lateinit var btnStartHosting: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_becomehost)

        // Настройка ActionBar (кнопка назад)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Стать арендодателем"

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        btnStartHosting = findViewById(R.id.btnStartHosting)
    }

    private fun setupClickListeners() {
        // Кнопка "Начать" - переход на экран добавления автомобиля
        btnStartHosting.setOnClickListener {
            val intent = Intent(this, AddCarActivity::class.java)
            startActivity(intent)
        }
    }

    // Обработка кнопки "Назад" в ActionBar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

//    // Обработка системной кнопки "Назад"
//    override fun onBackPressed() {
//        super.onBackPressed()
//        finish() // Возврат на экран настроек
//    }
}