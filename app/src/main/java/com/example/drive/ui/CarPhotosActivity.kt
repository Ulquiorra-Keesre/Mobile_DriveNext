package com.example.drive.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R
import com.example.drive.databinding.ActivityCarPhotosBinding
import com.example.drive.ui.SuccessActivity

class CarPhotosActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCarPhotosBinding

    private lateinit var btn_toSuccess: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initViews()
        setupHeader()
        setupClickListeners()
    }

    private fun initViews() {
        btn_toSuccess = findViewById(R.id.btn_toSuccess)
    }

    private fun setupClickListeners() {

        btn_toSuccess.setOnClickListener {
            val intent = Intent(this, SuccessActivity::class.java)
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