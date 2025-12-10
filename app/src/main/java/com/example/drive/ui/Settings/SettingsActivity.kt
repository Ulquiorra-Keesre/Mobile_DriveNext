package com.example.drive.ui.Settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drive.databinding.ActivitySettingsBinding
import com.example.drive.ui.Home.HomeActivity
import com.example.drive.ui.Profile.ProfileActivity


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupUserProfile()
        setupMenuItems()
        setupBottomNavigation()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Настройки"
    }

    private fun setupUserProfile() {
        // Данные пользователя
        binding.userNameTextView.text = "Иван Иванов"
        binding.userEmailTextView.text = "ivan@mtuci.ru"

        // Переход в профиль при клике на карточку профиля
        binding.avatarImageView.setOnClickListener {
            navigateToProfile()
        }

        // Клик на всю карточку профиля
        binding.root.findViewById<androidx.cardview.widget.CardView>(com.example.drive.R.id.root).setOnClickListener {
            navigateToProfile()
        }
    }

    private fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun setupMenuItems() {
        // 1. Мои бронирования
        binding.bookingsItem.setOnClickListener {
            showMessage("Мои бронирования")
            // TODO: Переход на экран бронирований
        }

        // 2. Тема
        binding.themeItem.setOnClickListener {
            showThemeDialog()
        }


        // 4. Подключить автомобиль
        binding.addCarItem.setOnClickListener {
            showMessage("Подключить автомобиль")
            // TODO: Переход на экран добавления автомобиля
        }

        // 5. Помощь
        binding.helpItem.setOnClickListener {
            showMessage("Помощь")
            // TODO: Переход на экран помощи
        }

        // 6. Пригласи друга
        binding.inviteItem.setOnClickListener {
            shareApp()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                com.example.drive.R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                com.example.drive.R.id.navigation_bookings -> {
                    // TODO: Переход на BookingsActivity
                    showMessage("Мои бронирования")
                    true
                }
                com.example.drive.R.id.navigation_settings -> {
                    // Уже на экране настроек
                    true
                }
                else -> false
            }
        }

        // Выделяем текущий пункт (Настройки)
        binding.bottomNavigation.selectedItemId = com.example.drive.R.id.navigation_settings
    }

    private fun showThemeDialog() {
        val themes = arrayOf("Светлая", "Темная")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Выберите тему")
            .setItems(themes) { _, which ->
                val selectedTheme = themes[which]
                saveThemeSetting(selectedTheme)
                showMessage("Тема изменена на: $selectedTheme")
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun saveThemeSetting(theme: String) {
        // TODO: Сохранить тему в SharedPreferences
    }

    private fun saveNotificationSetting(isEnabled: Boolean) {
        // TODO: Сохранить настройку уведомлений
        showMessage("Уведомления ${if (isEnabled) "включены" else "выключены"}")
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT,
            "Приглашаю тебя использовать Drive Rent! Скачай приложение по ссылке: https://drive-rent.com")
        startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}