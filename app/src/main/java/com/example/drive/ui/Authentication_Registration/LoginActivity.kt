package com.example.drive.ui.Authentication_Registration

import android.content.Intent
import android.content.SharedPreferences
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
import com.example.drive.ui.Home.HomeActivity
import com.example.drive.ui.NoInternetActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordLink: TextView
    private lateinit var googleLoginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
//        editor.putString("user_name", "Иван Иванов")
//        editor.putString("user_email", "ivan@mtuci.ru")
//        editor.apply()

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
            startActivity(Intent(this, RegisterActivity1::class.java))
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
        // Пока что используем mock-авторизацию для тестирования

        // Имитация успешного входа
        val loginSuccessful = true // Замените на реальную проверку с сервера

        if (loginSuccessful) {
            // Сохраняем статус входа
            saveLoginStatus(email)

            // Переходим на главный экран
            navigateToHome()

            Toast.makeText(this, "Успешный вход!", Toast.LENGTH_SHORT).show()
        } else {
            // Обработка ошибки входа
            Toast.makeText(this, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLoginStatus(email: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", true)
        editor.putString("user_email", email)
        editor.putString("user_name", email.split("@").first()) // Имя из email (до @)
        editor.apply()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        // Очищаем стек активностей, чтобы нельзя было вернуться назад к логину
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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