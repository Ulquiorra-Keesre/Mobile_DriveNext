package com.example.drive.ui.Authentication_Registration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.drive.R
import com.example.drive.ui.CongratulationsActivity
import com.example.drive.ui.NoInternetActivity
import java.io.File
import java.io.IOException
import java.util.Calendar
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Locale
import android.Manifest

import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher

class RegisterActivity3 : AppCompatActivity() {

    private lateinit var profileIcon: ImageView
    private lateinit var addPhotoButton: ImageView
    private lateinit var licenseNumberEditText: EditText
    private lateinit var issueDateEditText: EditText
    private lateinit var uploadLicenseButton: Button
    private lateinit var uploadPassportButton: Button
    private lateinit var nextButton: Button

    private lateinit var currentPhotoUri: Uri

    companion object {
        private const val REQUEST_GALLERY_PERMISSION = 1003
        private const val REQUEST_CAMERA_PERMISSION = 1004
    }

    // Лаунчер для съемки фото
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            profileIcon.setImageURI(currentPhotoUri)
        } else {
            Toast.makeText(this, "Не удалось сделать фото", Toast.LENGTH_SHORT).show()
        }
    }

    // Лаунчер для выбора фото из галереи
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileIcon.setImageURI(it)
            currentPhotoUri = it
        }
    }

    // Лаунчер для разрешения камеры
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(this, "Разрешение на камеру необходимо для съемки фото", Toast.LENGTH_SHORT).show()
        }
    }

    // Лаунчер для разрешения галереи
    private val requestGalleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageLauncher.launch("image/*")
        } else {
            Toast.makeText(this, "Разрешение необходимо для выбора фото", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickLicenseLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Toast.makeText(this, "Фото водительского удостоверения загружено", Toast.LENGTH_SHORT).show()
            // Сохраните uri для отправки
        }
    }

    private val pickPassportLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Toast.makeText(this, "Фото паспорта загружено", Toast.LENGTH_SHORT).show()
            // Сохраните uri для отправки
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register3)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        profileIcon = findViewById(R.id.profileIcon)
        addPhotoButton = findViewById(R.id.addPhotoButton)
        licenseNumberEditText = findViewById(R.id.licenseNumberEditText)
        issueDateEditText = findViewById(R.id.issueDateEditText)
        uploadLicenseButton = findViewById(R.id.upload_license)
        uploadPassportButton = findViewById(R.id.upload_passport)
        nextButton = findViewById(R.id.nextButton)
    }

    private fun setupClickListeners() {
        // Кнопка "Назад"
        findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Добавление фото профиля
        addPhotoButton.setOnClickListener {
            showImageSourceDialog()
        }

        // Выбор даты выдачи
        issueDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Загрузка фото водительского удостоверения
        uploadLicenseButton.setOnClickListener {
            openGalleryForLicense()
        }

        // Загрузка фото паспорта
        uploadPassportButton.setOnClickListener {
            openGalleryForPassport()
        }

        nextButton.setOnClickListener {
            if (!isNetworkAvailable()) {
                startActivity(Intent(this, NoInternetActivity::class.java))
                return@setOnClickListener
            }

            val licenseNumber = licenseNumberEditText.text.toString().trim()
            val issueDate = issueDateEditText.text.toString().trim()

            if (licenseNumber.isEmpty() || issueDate.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDate(issueDate)) {
                issueDateEditText.error = "Введите корректную дату (DD/MM/YYYY)"
                issueDateEditText.requestFocus()
                return@setOnClickListener
            }

            performRegistration(licenseNumber, issueDate)
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Сделать фото", "Выбрать из галереи")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Добавить фото")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> checkCameraPermissionAndTakePicture()
                1 -> checkGalleryPermissionAndPickImage()
            }
        }
        builder.show()
    }

    private fun checkCameraPermissionAndTakePicture() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent()
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkGalleryPermissionAndPickImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                pickImageLauncher.launch("image/*")
            }
            else -> {
                requestGalleryPermissionLauncher.launch(permission)
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val photoFile = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(this, "Не удалось создать файл для фото", Toast.LENGTH_SHORT).show()
            null
        }

        photoFile?.let { file ->
            currentPhotoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )
            takePictureLauncher.launch(currentPhotoUri)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Calendar.getInstance().time)
        val storageDir = getExternalFilesDir(null) ?: throw IOException("External storage not available")
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun openGalleryForLicense() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                pickLicenseLauncher.launch("image/*")
            }
            else -> {
                ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_GALLERY_PERMISSION)
            }
        }
    }

    private fun openGalleryForPassport() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                pickPassportLauncher.launch("image/*")
            }
            else -> {
                ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_GALLERY_PERMISSION)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_GALLERY_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Можно показать диалог выбора или сразу запустить нужный лаунчер
                    Toast.makeText(this, "Разрешение предоставлено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Разрешение необходимо для выбора фото", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                } else {
                    Toast.makeText(this, "Разрешение на камеру необходимо для съемки фото", Toast.LENGTH_SHORT).show()
                }
            }
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
                val formattedDate = String.format("%02d/%02d/%02d", selectedDay, selectedMonth + 1, selectedYear)
                issueDateEditText.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun isValidDate(date: String): Boolean {
        return Regex("^\\d{2}/\\d{2}/\\d{4}\$").matches(date)
    }

    private fun performRegistration(licenseNumber: String, issueDate: String) {
        Toast.makeText(this, "Регистрация завершена!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, CongratulationsActivity::class.java)

        // Передаем ВСЕ накопленные данные
        intent.putExtra("email", intent.getStringExtra("email") ?: "")
        intent.putExtra("password", intent.getStringExtra("password") ?: "")
        intent.putExtra("last_name", intent.getStringExtra("last_name") ?: "")
        intent.putExtra("first_name", intent.getStringExtra("first_name") ?: "")
        intent.putExtra("middle_name", intent.getStringExtra("middle_name") ?: "")
        intent.putExtra("dob", intent.getStringExtra("dob") ?: "")
        intent.putExtra("gender", intent.getStringExtra("gender") ?: "")

        // Добавляем данные из текущего экрана
        intent.putExtra("license_number", licenseNumber)
        intent.putExtra("issue_date", issueDate)

        startActivity(intent)
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