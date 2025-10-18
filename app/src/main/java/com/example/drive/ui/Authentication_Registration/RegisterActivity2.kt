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
import android.widget.RadioGroup
import android.widget.RadioButton
import android.os.Build
import android.net.NetworkCapabilities
import java.util.Calendar
import android.app.DatePickerDialog

class RegisterActivity2 : AppCompatActivity() {

    private lateinit var lastNameEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var middleNameEditText: EditText
    private lateinit var dobEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var maleRadioButton: RadioButton
    private lateinit var femaleRadioButton: RadioButton
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        lastNameEditText = findViewById(R.id.lastNameEditText)
        firstNameEditText = findViewById(R.id.firstNameEditText)
        middleNameEditText = findViewById(R.id.middleNameEditText)
        dobEditText = findViewById(R.id.dobEditText)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        maleRadioButton = findViewById(R.id.maleRadioButton)
        femaleRadioButton = findViewById(R.id.femaleRadioButton)
        nextButton = findViewById(R.id.nextButton)
    }

    private fun setupClickListeners() {
        // Кнопка "Назад"
        findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.backButton).setOnClickListener {
            finish() // закрываем RegisterActivity2


        }


        dobEditText.setOnClickListener {
            showDatePickerDialog()
        }



        nextButton.setOnClickListener {
            if (!isNetworkAvailable()) {
                startActivity(Intent(this, NoInternetActivity::class.java))
                return@setOnClickListener
            }

            val lastName = lastNameEditText.text.toString().trim()
            val firstName = firstNameEditText.text.toString().trim()
            val middleName = middleNameEditText.text.toString().trim()
            val dob = dobEditText.text.toString().trim()

            if (lastName.isEmpty() || firstName.isEmpty() || middleName.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val gender = when (selectedGenderId) {
                R.id.maleRadioButton -> "Мужской"
                R.id.femaleRadioButton -> "Женский"
                else -> {
                    Toast.makeText(this, "Выберите пол", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Валидация даты (упрощённая)
            if (!isValidDate(dob)) {
                dobEditText.error = "Введите корректную дату (DD/MM/YYYY)"
                dobEditText.requestFocus()
                return@setOnClickListener
            }

            performRegistration(lastName, firstName, middleName, dob, gender)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Форматируем дату как DD/MM/YYYY
                val formattedDate = String.format("%02d/%02d/%02d", selectedDay, selectedMonth + 1, selectedYear)
                dobEditText.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun isValidDate(date: String): Boolean {
        // Простая проверка формата DD/MM/YYYY
        return Regex("^\\d{2}/\\d{2}/\\d{4}\$").matches(date)
    }

    private fun performRegistration(
        lastName: String,
        firstName: String,
        middleName: String,
        dob: String,
        gender: String
    ) {
        // TODO: отправка данных на сервер
        Toast.makeText(this, "Регистрация: $firstName $lastName ($gender)", Toast.LENGTH_SHORT).show()

        // После успешной регистрации:
        // 1. Сохранить данные пользователя (если нужно)
        // 2. Перейти на HomeActivity
        // 3. Закрыть RegisterActivity2
        startActivity(Intent(this, RegisterActivity3::class.java))
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