package com.example.drive.ui.Authentication_Registration

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R
import com.example.drive.ui.NoInternetActivity
import com.example.drive.ui.Authentication_Registration.RegisterActivity // ← убедитесь в правильном пути

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordLink: TextView
    private lateinit var googleLoginButton: Button
    private lateinit var registerLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink)
        googleLoginButton = findViewById(R.id.googleLoginButton)
        registerLink = findViewById(R.id.registerLink)
    }

    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            if (!isNetworkAvailable()) {
                startActivity(Intent(this, NoInternetActivity::class.java))
                return@setOnClickListener
            }

            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                emailEditText.error = "Введите корректный email (например, user@example.com)"
                emailEditText.requestFocus()
                return@setOnClickListener
            }

            // Валидация пароля
            val passwordError = validatePassword(password)
            if (passwordError != null) {
                passwordEditText.error = passwordError
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            performLogin(email, password)
        }

        forgotPasswordLink.setOnClickListener {
            Toast.makeText(this, "Переход на восстановление пароля...", Toast.LENGTH_SHORT).show()
            // TODO: startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        googleLoginButton.setOnClickListener {
            if (!isNetworkAvailable()) {
                startActivity(Intent(this, NoInternetActivity::class.java))
                return@setOnClickListener
            }
            Toast.makeText(this, "Вход через Google...", Toast.LENGTH_SHORT).show()
            // TODO: реализовать Google Sign-In
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): String? {

        if (password.length < 8) {
            return "Пароль должен содержать минимум 8 символов"
        }

        if (!password.any { it.isDigit() }) {
            return "Пароль должен содержать хотя бы одну цифру"
        }

        if (!password.any { it.isUpperCase() }) {
            return "Пароль должен содержать хотя бы одну заглавную букву"
        }

        if (!password.any { it in "!@#$%^&*()_+-=[]{}|;:,.<>?" }) {
            return "Пароль должен содержать хотя бы один специальный символ"
        }
        return null
    }

    private fun performLogin(email: String, password: String) {
        // TODO: реализовать отправку данных на сервер (Retrofit, Volley и т.д.)
        Toast.makeText(this, "Вход с email: $email", Toast.LENGTH_SHORT).show()
    }

    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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