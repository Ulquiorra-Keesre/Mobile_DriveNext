package com.example.drive.ui.Authentication_Registration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R
import com.example.drive.ui.NoInternetActivity
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager

class RegisterActivity1 : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var termsCheckbox: CheckBox
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register1)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        termsCheckbox = findViewById(R.id.termsCheckbox)
        nextButton = findViewById(R.id.nextButton)
    }

    private fun setupClickListeners() {
        // Кнопка "Назад"
        findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.backButton).setOnClickListener {
            finish() // закрываем RegisterActivity
        }

        nextButton.setOnClickListener {
            if (!isNetworkAvailable()) {
                startActivity(Intent(this, NoInternetActivity::class.java))
                return@setOnClickListener
            }

            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Валидация
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                emailEditText.error = "Введите корректный email"
                emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                confirmPasswordEditText.error = "Пароли не совпадают"
                confirmPasswordEditText.requestFocus()
                return@setOnClickListener
            }

            val passwordError = validatePassword(password)
            if (passwordError != null) {
                passwordEditText.error = passwordError
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            if (!termsCheckbox.isChecked) {
                Toast.makeText(this, "Вы должны принять условия", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performRegistration(email, password)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): String? {
        if (password.length < 8) return "Пароль должен быть не менее 8 символов"
        if (!password.any { it.isDigit() }) return "Пароль должен содержать цифру"
        if (!password.any { it.isUpperCase() }) return "Пароль должен содержать заглавную букву"
        if (!password.any { it in "!@#$%^&*()_+-=[]{}|;:,.<>?" }) return "Пароль должен содержать спецсимвол"
        return null
    }

    private fun performRegistration(email: String, password: String) {
        Toast.makeText(this, "Регистрация: $email", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, RegisterActivity2::class.java)
        intent.putExtra("email", email)      // ← Передаем email
        intent.putExtra("password", password) // ← Передаем пароль
        startActivity(intent)
    }

    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
                capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                @Suppress("DEPRECATION")
                connectivityManager.activeNetworkInfo?.isConnected == true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}