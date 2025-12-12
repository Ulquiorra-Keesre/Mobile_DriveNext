package com.example.drive.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.drive.R
import com.example.drive.databinding.ActivityAddCar2Binding
import com.google.android.material.textfield.TextInputEditText

class AddCar2Activity: AppCompatActivity() {

    private lateinit var binding: ActivityAddCar2Binding
    private lateinit var btn_toCarPhotos: Button

    // Поля ввода
    private lateinit var etYear: TextInputEditText
    private lateinit var etBrand: TextInputEditText
    private lateinit var etModel: TextInputEditText
    private lateinit var autoCompleteTransmission: AutoCompleteTextView
    private lateinit var etMileage: TextInputEditText
    private lateinit var etDescription: TextInputEditText

    // Варианты трансмиссии для выпадающего списка
    private val transmissionOptions = arrayOf("Механическая", "Автоматическая", "Роботизированная", "Вариатор")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCar2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setupHeader()
        setupTransmissionDropdown()
        setupTextWatchers()
        setupClickListeners()
    }

    private fun initViews() {
        btn_toCarPhotos = binding.btnToCarPhotos

        // Инициализация полей ввода
        etYear = binding.etYear
        etBrand = binding.etBrand
        etModel = binding.etModel
        autoCompleteTransmission = binding.autoCompleteTransmission
        etMileage = binding.etMileage
        etDescription = binding.etDescription
    }


    private fun setupTransmissionDropdown() {
        // Настройка выпадающего списка для трансмиссии
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            transmissionOptions
        )
        autoCompleteTransmission.setAdapter(adapter)

        // Слушатель изменений в выпадающем списке
        autoCompleteTransmission.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateAllFields()
            }
        })
    }

    private fun setupTextWatchers() {
        // Общий слушатель для всех текстовых полей
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateAllFields()
            }
        }

        etYear.addTextChangedListener(textWatcher)
        etBrand.addTextChangedListener(textWatcher)
        etModel.addTextChangedListener(textWatcher)
        etMileage.addTextChangedListener(textWatcher)
        etDescription.addTextChangedListener(textWatcher)
    }

    private fun validateAllFields() {
        // Получаем значения из всех полей
        val year = etYear.text.toString().trim()
        val brand = etBrand.text.toString().trim()
        val model = etModel.text.toString().trim()
        val transmission = autoCompleteTransmission.text.toString().trim()
        val mileage = etMileage.text.toString().trim()
        val description = etDescription.text.toString().trim()

        // Проверяем, что все поля заполнены
        val allFieldsFilled = year.isNotEmpty() &&
                brand.isNotEmpty() &&
                model.isNotEmpty() &&
                transmission.isNotEmpty() &&
                mileage.isNotEmpty() &&
                description.isNotEmpty()

        // Дополнительная валидация года (должен быть 4 цифры)
        val isYearValid = year.length == 4 && year.toIntOrNull() in 1900..2024

        if (allFieldsFilled && isYearValid) {
            // Активируем кнопку
            btn_toCarPhotos.isEnabled = true
            //btn_toCarPhotos.setBackgroundColor(ContextCompat.getColor(this, R.color.button_active))
            btn_toCarPhotos.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2A1246"))
        } else {
            // Деактивируем кнопку
            btn_toCarPhotos.isEnabled = false
            //btn_toCarPhotos.setBackgroundColor(ContextCompat.getColor(this, R.color.button_inactive))
            btn_toCarPhotos.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CCCCCC"))
        }
    }

    private fun setupClickListeners() {
        // Кнопка "Отправить" - переход к загрузке фото
        btn_toCarPhotos.setOnClickListener {
            if (validateAndCollectData()) {
                // Собираем все данные
                val year = etYear.text.toString().trim()
                val brand = etBrand.text.toString().trim()
                val model = etModel.text.toString().trim()
                val transmission = autoCompleteTransmission.text.toString().trim()
                val mileage = etMileage.text.toString().trim().toIntOrNull() ?: 0
                val description = etDescription.text.toString().trim()

                // Получаем адрес из предыдущего экрана
                val address = intent.getStringExtra("CAR_ADDRESS") ?: ""

                // Передаем все данные в следующий экран
                val intent = Intent(this, CarPhotosActivity::class.java).apply {
                    putExtra("CAR_ADDRESS", address)
                    putExtra("CAR_YEAR", year)
                    putExtra("CAR_BRAND", brand)
                    putExtra("CAR_MODEL", model)
                    putExtra("CAR_TRANSMISSION", transmission)
                    putExtra("CAR_MILEAGE", mileage.toString())
                    putExtra("CAR_DESCRIPTION", description)
                }
                startActivity(intent)
            }
        }
    }

    private fun validateAndCollectData(): Boolean {
        // Валидация каждого поля
        val year = etYear.text.toString().trim()
        if (year.isEmpty()) {
            etYear.error = "Введите год выпуска"
            return false
        }
        if (year.length != 4 || year.toIntOrNull() !in 1900..2024) {
            etYear.error = "Введите корректный год (1900-2024)"
            return false
        }

        if (etBrand.text.toString().trim().isEmpty()) {
            etBrand.error = "Введите марку автомобиля"
            return false
        }

        if (etModel.text.toString().trim().isEmpty()) {
            etModel.error = "Введите модель автомобиля"
            return false
        }

        if (autoCompleteTransmission.text.toString().trim().isEmpty()) {
            autoCompleteTransmission.error = "Выберите тип трансмиссии"
            return false
        }

        val mileage = etMileage.text.toString().trim()
        if (mileage.isEmpty()) {
            etMileage.error = "Введите пробег"
            return false
        }
        if (mileage.toIntOrNull() == null || mileage.toInt() < 0) {
            etMileage.error = "Введите корректный пробег"
            return false
        }

        if (etDescription.text.toString().trim().isEmpty()) {
            etDescription.error = "Введите описание автомобиля"
            return false
        }

        return true
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