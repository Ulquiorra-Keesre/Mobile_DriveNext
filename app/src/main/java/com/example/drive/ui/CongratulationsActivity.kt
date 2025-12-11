package com.example.drive.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.drive.R
import com.example.drive.ui.Home.HomeActivity
import android.widget.Toast
import kotlin.apply

class CongratulationsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congratulations)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Получаем ВСЕ данные из Intent
        val email = intent.getStringExtra("email") ?: ""
        val firstName = intent.getStringExtra("first_name") ?: ""
        val lastName = intent.getStringExtra("last_name") ?: ""
        val middleName = intent.getStringExtra("middle_name") ?: ""

        // Формируем полное имя
        val fullName = if (middleName.isNotEmpty()) {
            "$lastName $firstName $middleName"
        } else {
            "$lastName $firstName"
        }

        // Сохраняем в SharedPreferences
        saveUserData(email, fullName)

        setupClickListeners()
    }

    private fun saveUserData(email: String, name: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", true)
        editor.putString("user_email", email)
        editor.putString("user_name", name)
        editor.putString("join_date", "Присоединился в ${getCurrentMonthYear()}")
        editor.apply()

        Toast.makeText(this, "Добро пожаловать, $name!", Toast.LENGTH_SHORT).show()
    }

    private fun getCurrentMonthYear(): String {
        val calendar = java.util.Calendar.getInstance()
        val month = calendar.get(java.util.Calendar.MONTH)
        val year = calendar.get(java.util.Calendar.YEAR)

        val months = arrayOf(
            "январе", "феврале", "марте", "апреле", "мае", "июне",
            "июле", "августе", "сентябре", "октябре", "ноябре", "декабре"
        )

        return "${months[month]} $year"
    }

    private fun setupClickListeners() {
        val startButton = findViewById<Button>(R.id.btnGetStarted)
        startButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)

            // Передаем данные в HomeActivity (опционально)
            intent.putExtra("user_name", sharedPreferences.getString("user_name", ""))
            intent.putExtra("user_email", sharedPreferences.getString("user_email", ""))

            startActivity(intent)
            finish()
        }
    }

    private fun saveLoginStatus() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", true)

        // Сохраняем данные пользователя
        // Получаем данные из Intent или предыдущих экранов
        //val email = intent.getStringExtra("email") ?: ""
        //val name = intent.getStringExtra("name") ?: ""

        //editor.putString("user_email", email)
        //editor.putString("user_name", name)
        editor.putBoolean("registration_completed", true)
        editor.apply()
    }
}


