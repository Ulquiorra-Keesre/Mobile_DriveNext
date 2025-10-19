package com.example.drive.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R
import com.example.drive.ui.HomeActivity

class CongratulationsActivity : AppCompatActivity() {

    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congratulations)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        nextButton = findViewById(R.id.nextButton)
    }

    private fun setupClickListeners() {
        nextButton.setOnClickListener {
            // Переход на главный экран
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}