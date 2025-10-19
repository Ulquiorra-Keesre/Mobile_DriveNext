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
        private const val REQUEST_IMAGE_CAPTURE = 1001
        private const val REQUEST_PICK_IMAGE = 1002
        private const val REQUEST_GALLERY_PERMISSION = 1003
        private const val REQUEST_PICK_LICENSE = 1004
        private const val REQUEST_PICK_PASSPORT = 1005
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            profileIcon.setImageURI(currentPhotoUri)
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileIcon.setImageURI(it)
            currentPhotoUri = it
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
                0 -> dispatchTakePictureIntent()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        val photoFile = createImageFile()
        currentPhotoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        takePictureLauncher.launch(currentPhotoUri)
    }



private fun hasGalleryPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(null) ?: throw IOException("External storage not available")
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir
        )
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_GALLERY_PERMISSION)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_IMAGE)
        }
    }

    private fun openGalleryForLicense() {
        openGalleryWithResult(REQUEST_PICK_LICENSE)
    }

    private fun openGalleryForPassport() {
        openGalleryWithResult(REQUEST_PICK_PASSPORT)
    }

    private fun openGalleryWithResult(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_GALLERY_PERMISSION)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    profileIcon.setImageURI(currentPhotoUri)
                }
                REQUEST_PICK_IMAGE -> {
                    data?.data?.let { uri ->
                        profileIcon.setImageURI(uri)
                        currentPhotoUri = uri
                    }
                }
                REQUEST_PICK_LICENSE -> {
                    Toast.makeText(this, "Фото водительского удостоверения загружено", Toast.LENGTH_SHORT).show()
                    // Здесь можно сохранить URI или битмап для отправки на сервер
                }
                REQUEST_PICK_PASSPORT -> {
                    Toast.makeText(this, "Фото паспорта загружено", Toast.LENGTH_SHORT).show()
                    // Здесь можно сохранить URI или битмап для отправки на сервер
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Разрешение необходимо для выбора фото", Toast.LENGTH_SHORT).show()
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
        // TODO: отправка данных на сервер
        Toast.makeText(this, "Регистрация завершена!", Toast.LENGTH_SHORT).show()

        // Переход на главный экран
        startActivity(Intent(this, CongratulationsActivity::class.java))
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
