package com.example.drive.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R
import com.example.drive.ui.AddCarActivity
import com.example.drive.databinding.ActivityBecomehostBinding
import android.view.LayoutInflater


class BecomeHostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBecomehostBinding
    private lateinit var btnStartHosting: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBecomehostBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initViews()
        setupHeader()
        setupClickListeners()
    }

    private fun initViews() {
        btnStartHosting = findViewById(R.id.btnStartHosting)
    }

    private fun setupClickListeners() {
        // Кнопка переход на экран добавления автомобиля
        btnStartHosting.setOnClickListener {
            val intent = Intent(this, AddCarActivity::class.java)
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