package com.example.drive.ui

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.drive.R
import com.example.drive.databinding.ActivityCarPhotosBinding

class CarPhotosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarPhotosBinding
    private lateinit var btn_toSuccess: Button

    // Массивы для фото контейнеров
    private lateinit var photoContainers: Array<FrameLayout>
    private lateinit var photoImages: Array<ImageView>
    private lateinit var deleteButtons: Array<ImageView>
    private lateinit var addIcons: Array<ImageView>

    // Список URI выбранных фотографий
    private val selectedPhotos = mutableListOf<Uri>()
    private var currentPhotoIndex = -1 // Индекс текущего редактируемого слота

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 100
        private const val MAX_PHOTOS = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setupHeader()
        setupPhotoContainers()
        setupClickListeners()
        updateNextButtonState()
    }

    private fun initViews() {
        btn_toSuccess = binding.btnToSuccess

        // Инициализация массивов
        photoContainers = arrayOf(
            binding.photoContainer1,
            binding.photoContainer2,
            binding.photoContainer3,
            binding.photoContainer4,
            binding.photoContainer5
        )

        photoImages = arrayOf(
            binding.photo1,
            binding.photo2,
            binding.photo3,
            binding.photo4,
            binding.photo5
        )

        deleteButtons = arrayOf(
            binding.deletePhoto1,
            binding.deletePhoto2,
            binding.deletePhoto3,
            binding.deletePhoto4,
            binding.deletePhoto5
        )

        addIcons = arrayOf(
            binding.addIcon1,
            binding.addIcon2,
            binding.addIcon3,
            binding.addIcon4,
            binding.addIcon5
        )
    }

    private fun setupHeader() {
        // Настройка кнопки назад
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupPhotoContainers() {
        // Настройка кликов на все контейнеры
        for (i in photoContainers.indices) {
            photoContainers[i].setOnClickListener {
                if (photoImages[i].visibility == android.view.View.VISIBLE) {
                    // Если фото уже есть, можно показать предпросмотр или перезаписать
                    selectPhoto(i)
                } else {
                    // Если слота пустой, добавляем фото
                    selectPhoto(i)
                }
            }

            // Настройка кнопок удаления
            deleteButtons[i].setOnClickListener {
                removePhoto(i)
            }
        }
    }

    private fun setupClickListeners() {
        // Кнопка "Далее"
        btn_toSuccess.setOnClickListener {
            if (selectedPhotos.isNotEmpty()) {
                // Собираем все данные из предыдущих экранов
                val address = intent.getStringExtra("CAR_ADDRESS") ?: ""
                val year = intent.getStringExtra("CAR_YEAR") ?: ""
                val brand = intent.getStringExtra("CAR_BRAND") ?: ""
                val model = intent.getStringExtra("CAR_MODEL") ?: ""
                val transmission = intent.getStringExtra("CAR_TRANSMISSION") ?: ""
                val mileage = intent.getStringExtra("CAR_MILEAGE") ?: ""
                val description = intent.getStringExtra("CAR_DESCRIPTION") ?: ""

                // Получаем URI первой фотографии (если есть)
                val firstPhotoUri = if (selectedPhotos.isNotEmpty()) selectedPhotos[0].toString() else ""

                // Передаем все данные в SuccessActivity
                val intent = Intent(this, SuccessActivity::class.java).apply {
                    // Основные данные автомобиля
                    putExtra("CAR_ADDRESS", address)
                    putExtra("CAR_YEAR", year)
                    putExtra("CAR_BRAND", brand)
                    putExtra("CAR_MODEL", model)
                    putExtra("CAR_TRANSMISSION", transmission)
                    putExtra("CAR_MILEAGE", mileage)
                    putExtra("CAR_DESCRIPTION", description)

                    // Данные фотографий
                    putExtra("FIRST_PHOTO_URI", firstPhotoUri)
                    putExtra("PHOTOS_COUNT", selectedPhotos.size.toString())

                    // Передаем все URI фотографий как список
                    val photoUris = selectedPhotos.map { it.toString() }.toTypedArray()
                    putExtra("PHOTO_URIS", photoUris)
                }
                startActivity(intent)
            }
        }
    }

    private fun selectPhoto(index: Int) {
        currentPhotoIndex = index
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    private fun removePhoto(index: Int) {
        // Удаляем фото из списка
        if (index < selectedPhotos.size) {
            selectedPhotos.removeAt(index)
        }

        // Обновляем UI
        updatePhotoContainers()
        updateNextButtonState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                if (currentPhotoIndex != -1) {
                    // Если редактируем существующее фото
                    if (currentPhotoIndex < selectedPhotos.size) {
                        selectedPhotos[currentPhotoIndex] = uri
                    } else {
                        // Добавляем новое фото
                        selectedPhotos.add(uri)
                    }

                    updatePhotoContainers()
                    updateNextButtonState()
                }
            }
        }
        currentPhotoIndex = -1
    }

    private fun updatePhotoContainers() {
        // Сначала скрываем все
        for (i in photoImages.indices) {
            photoImages[i].visibility = android.view.View.GONE
            deleteButtons[i].visibility = android.view.View.GONE
            addIcons[i].visibility = android.view.View.VISIBLE
        }

        // Показываем добавленные фото
        for (i in selectedPhotos.indices) {
            if (i < photoImages.size) {
                photoImages[i].setImageURI(selectedPhotos[i])
                photoImages[i].visibility = android.view.View.VISIBLE
                deleteButtons[i].visibility = android.view.View.VISIBLE
                addIcons[i].visibility = android.view.View.GONE
            }
        }

        // Скрываем лишние контейнеры, если фото уже 5
        if (selectedPhotos.size >= MAX_PHOTOS) {
            for (i in selectedPhotos.size until photoContainers.size) {
                photoContainers[i].visibility = android.view.View.GONE
            }
        } else {
            // Показываем все контейнеры
            for (container in photoContainers) {
                container.visibility = android.view.View.VISIBLE
            }
        }
    }

    private fun updateNextButtonState() {
        if (selectedPhotos.isNotEmpty()) {
            // Активируем кнопку
            btn_toSuccess.isEnabled = true
            //btn_toSuccess.setBackgroundColor(ContextCompat.getColor(this, R.color.button_active))
            btn_toSuccess.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2A1246"))
        } else {
            // Деактивируем кнопку
            btn_toSuccess.isEnabled = false
            //btn_toSuccess.setBackgroundColor(ContextCompat.getColor(this, R.color.button_inactive))
            btn_toSuccess.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CCCCCC"))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}