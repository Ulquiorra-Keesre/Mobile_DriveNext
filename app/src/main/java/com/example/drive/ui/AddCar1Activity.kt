package com.example.drive.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.drive.R
import com.example.drive.databinding.ActivityAddCar1Binding
import com.google.android.material.textfield.TextInputEditText

class AddCar1Activity: AppCompatActivity() {

    private lateinit var binding: ActivityAddCar1Binding
    private lateinit var btn_toAddCar2: Button
    private lateinit var etAddress: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCar1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setupHeader()
        setupTextWatcher()
        setupClickListeners()
    }

    private fun initViews() {
        btn_toAddCar2 = binding.btnToAddCar2
        etAddress = binding.etAddress
    }


    private fun setupTextWatcher() {
        // Слушатель изменений текста в поле адреса
        etAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateInput()
            }
        })
    }

    private fun validateInput() {
        val address = etAddress.text.toString().trim()

        if (address.isNotEmpty()) {
            // Активируем кнопку
            btn_toAddCar2.isEnabled = true
            //btn_toAddCar2.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color))
            // Если у вас есть цвет в ресурсах, иначе используйте:
            btn_toAddCar2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2A1246"))
        } else {
            // Деактивируем кнопку
            btn_toAddCar2.isEnabled = false
            //btn_toAddCar2.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_light))
            btn_toAddCar2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CCCCCC"))
        }
    }

    private fun setupClickListeners() {
        // Кнопка переход на следующий экран
        btn_toAddCar2.setOnClickListener {
            val address = etAddress.text.toString().trim()

            if (address.isNotEmpty()) {
                // TODO: Сохраняем адрес во временное хранилище или ViewModel
                // Пока просто передаем через Intent
                val intent = Intent(this, AddCar2Activity::class.java).apply {
                    putExtra("CAR_ADDRESS", address)
                }
                startActivity(intent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupHeader() {
        // Настройка кнопки назад
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }
}