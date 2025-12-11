package com.example.drive.ui.Settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.drive.R
import com.example.drive.ui.BecomeHostActivity
import com.example.drive.ui.Home.HomeActivity
import com.example.drive.ui.Profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvUserName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var profileBtn: ImageView
    private lateinit var hostBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        initViews()
        setupClickListeners()
        loadUserData()
        setupBottomNavigation()
    }

    private fun initViews() {
        tvUserName = findViewById(R.id.tvUserName)
        tvEmail = findViewById(R.id.tvEmail)
        profileBtn = findViewById(R.id.btn_toProfile)
        hostBtn = findViewById(R.id.btn_toHost)
    }

    private fun setupClickListeners() {
        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        hostBtn.setOnClickListener {
            val intent = Intent(this, BecomeHostActivity::class.java)
            startActivity(intent)
        }


    }

    private fun loadUserData() {
        val name = sharedPreferences.getString("user_name", "Иван Иванов")
        val email = sharedPreferences.getString("user_email", "user@example.com")

        // Отладка
        Log.d("SettingsActivity", "Name: $name, Email: $email")

        // Проверка на null перед установкой текста
        if (tvUserName != null && tvEmail != null) {
            tvUserName.text = name
            tvEmail.text = email
        } else {
            Log.e("SettingsActivity", "TextView not found!")
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.navigation_settings

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.navigation_bookings -> {
                    // TODO: Переход на BookingsActivity
                    true
                }
                R.id.navigation_settings -> {
                    true
                }
                else -> false
            }
        }
    }


    override fun onResume() {
        super.onResume()
        loadUserData() // Обновляем данные при возвращении на экран
    }
}