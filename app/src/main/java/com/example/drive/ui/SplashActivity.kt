package com.example.drive.ui

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R
import android.content.Context
import com.example.drive.ui.Onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("onboarding_completed", false)
            .putString("access_token", null)
            .apply()

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
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish()
            }
        }, 2500)
    }

    private fun isUserLoggedIn(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val token = prefs.getString("access_token", null)
        return !token.isNullOrBlank()
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
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return !prefs.getBoolean("onboarding_completed", false)
    }
}