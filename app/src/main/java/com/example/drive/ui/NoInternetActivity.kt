package com.example.drive.ui

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.R

class NoInternetActivity : AppCompatActivity() {

    private lateinit var retryButton: Button
    private lateinit var iconImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        initViews()
        setupRetryButton()
    }

    private fun initViews() {
        retryButton = findViewById(R.id.retryButton)
        iconImageView = findViewById(R.id.iconImageView)
    }

    private fun setupRetryButton() {
        retryButton.setOnClickListener {
            if (isNetworkAvailable()) {
                finish()
            } else {
                Toast.makeText(this, "Подключение не восстановлено", Toast.LENGTH_SHORT).show()
            }
        }
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
}