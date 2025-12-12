package com.example.drive.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R
import com.example.drive.databinding.ActivitySuccessBinding
import com.example.drive.ui.Home.HomeActivity

class CarPhotosActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySuccessBinding

    private lateinit var btn_toHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initViews()
        setupHeader()
        setupClickListeners()
    }

    private fun initViews() {
        btn_toHome = findViewById(R.id.btn_toHome)
    }

    private fun setupClickListeners() {

        btn_toHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
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