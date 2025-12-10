package com.example.drive.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.databinding.ActivityCongratulationsBinding
import com.example.drive.ui.Home.HomeActivity

class CongratulationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCongratulationsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCongratulationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnGetStarted.setOnClickListener {
            // Сохраняем, что пользователь вошел
            saveLoginStatus()

            // Переходим на главный экран
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun saveLoginStatus() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", true)

        // Сохраняем данные пользователя
        // Получаем данные из Intent или предыдущих экранов
        val email = intent.getStringExtra("email") ?: ""
        val name = intent.getStringExtra("name") ?: ""

        editor.putString("user_email", email)
        editor.putString("user_name", name)
        editor.putBoolean("registration_completed", true)
        editor.apply()
    }
}