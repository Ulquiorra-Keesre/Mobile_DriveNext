package com.example.drive.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R
import com.example.drive.databinding.ActivityAddCar2Binding
import com.example.drive.ui.CarPhotosActivity

class AddCar2Activity: AppCompatActivity() {

    private lateinit var binding: ActivityAddCar2Binding

    private lateinit var btn_toCarPhotos: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCar2Binding.inflate(layoutInflater)
        setContentView(binding.root)



        initViews()
        setupHeader()
        setupClickListeners()
    }

    private fun initViews() {
        btn_toCarPhotos = findViewById(R.id.btn_toCarPhotos)
    }

    private fun setupClickListeners() {

        btn_toCarPhotos.setOnClickListener {
            val intent = Intent(this, CarPhotosActivity::class.java)
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