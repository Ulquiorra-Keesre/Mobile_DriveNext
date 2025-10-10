package com.example.drive

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import android.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Отображаем экран загрузки 2.5 секунды (2500 мс), как компромисс между 2 и 3 секундами
        window.decorView.postDelayed({
            if (isUserLoggedIn()) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2500)
    }

    private fun isUserLoggedIn(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val token = prefs.getString("access_token", null)
        return !token.isNullOrBlank()
    }
}