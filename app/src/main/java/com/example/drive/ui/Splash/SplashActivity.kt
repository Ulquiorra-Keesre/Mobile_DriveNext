package com.example.drive.ui.Splash

import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R
import com.example.drive.ui.Authentication_Registration.GettingStartedActivity
import com.example.drive.ui.Home.HomeActivity
import com.example.drive.ui.NoInternetActivity
import com.example.drive.ui.Onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Инициализация настроек по умолчанию (только при первом запуске)
        if (!sharedPreferences.contains("onboarding_completed")) {
            sharedPreferences.edit()
                .putBoolean("onboarding_completed", false)
                .putBoolean("is_logged_in", false)
                .apply()
        }

        window.decorView.postDelayed({
            if (!isNetworkAvailable()) {
                startActivity(Intent(this, NoInternetActivity::class.java))
                // Оставляем SplashActivity активной — пользователь вернётся сюда после NoInternetActivity
            } else {
                // Все остальные случаи — завершаем Splash
                if (isUserLoggedIn()) {
                    startActivity(Intent(this, HomeActivity::class.java))
                } else if (shouldShowOnboarding()) {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                } else {
                    startActivity(Intent(this, GettingStartedActivity::class.java))
                }
                finish()
            }
        }, 2500)
    }

    private fun isUserLoggedIn(): Boolean {
        // Проверяем в UserPrefs, а не в PreferenceManager
        return sharedPreferences.getBoolean("is_logged_in", false)
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
            false // при любой ошибке — считаем, что интернета нет
        }
    }

    private fun shouldShowOnboarding(): Boolean {
        return !sharedPreferences.getBoolean("onboarding_completed", false)
    }
}