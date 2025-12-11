package com.example.drive.ui.Profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.drive.R
import com.example.drive.ui.Authentication_Registration.LoginActivity
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvJoinDate: TextView
    private lateinit var tvEmail: TextView
    private lateinit var ivAvatar: ImageView
    private lateinit var tvChangeAvatar: TextView
    private lateinit var tvChangePassword: TextView
    private lateinit var tLogout: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private val PICK_IMAGE_REQUEST = 100
    private val CAMERA_REQUEST = 200
    private val PERMISSION_REQUEST_CAMERA = 300
    private val PERMISSION_REQUEST_GALLERY = 400

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        Log.d("ProfileActivity", "=== onCreate started ===")

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        initViews()
        loadUserData()
        setupClickListeners()

        Log.d("ProfileActivity", "=== onCreate completed ===")
    }

    private fun initViews() {
        Log.d("ProfileActivity", "=== initViews started ===")

        try {
            Log.d("ProfileActivity", "Finding tvUserName...")
            tvUserName = findViewById(R.id.tvUserName)
            Log.d("ProfileActivity", "tvUserName found: ${tvUserName != null}, type: ${tvUserName::class.java.simpleName}")

            Log.d("ProfileActivity", "Finding tvJoinDate...")
            tvJoinDate = findViewById(R.id.tvJoinDate)
            Log.d("ProfileActivity", "tvJoinDate found: ${tvJoinDate != null}, type: ${tvJoinDate::class.java.simpleName}")

            Log.d("ProfileActivity", "Finding tvEmail...")
            tvEmail = findViewById(R.id.tvEmail)
            Log.d("ProfileActivity", "tvEmail found: ${tvEmail != null}, type: ${tvEmail::class.java.simpleName}")

            Log.d("ProfileActivity", "Finding ivAvatar...")
            ivAvatar = findViewById(R.id.ivAvatar)
            Log.d("ProfileActivity", "ivAvatar found: ${ivAvatar != null}, type: ${ivAvatar::class.java.simpleName}")

            Log.d("ProfileActivity", "Finding tvChangeAvatar...")
            tvChangeAvatar = findViewById(R.id.tvChangeAvatar)
            Log.d("ProfileActivity", "tvChangeAvatar found: ${tvChangeAvatar != null}, type: ${tvChangeAvatar::class.java.simpleName}")

            Log.d("ProfileActivity", "Finding tvChangePassword...")
            tvChangePassword = findViewById(R.id.tvChangePassword)
            Log.d("ProfileActivity", "tvChangePassword found: ${tvChangePassword != null}, type: ${tvChangePassword::class.java.simpleName}")

            Log.d("ProfileActivity", "Finding profileLogout...")
            tLogout = findViewById(R.id.profileLogout)
            Log.d("ProfileActivity", "profileLogout found: ${tLogout != null}, type: ${tLogout::class.java.simpleName}")

            Log.d("ProfileActivity", "=== All views initialized successfully ===")

        } catch (e: Exception) {
            Log.e("ProfileActivity", "ERROR in initViews: ${e.message}", e)
            e.printStackTrace()
            Toast.makeText(this, "Ошибка загрузки интерфейса: ${e.message}", Toast.LENGTH_LONG).show()

            // Показываем, какой элемент вызывает проблему
            val problematicElement = when {
                !::tvUserName.isInitialized -> "tvUserName"
                !::tvJoinDate.isInitialized -> "tvJoinDate"
                !::tvEmail.isInitialized -> "tvEmail"
                !::ivAvatar.isInitialized -> "ivAvatar"
                !::tvChangeAvatar.isInitialized -> "tvChangeAvatar"
                !::tvChangePassword.isInitialized -> "tvChangePassword"
                !::tLogout.isInitialized -> "profileLogout"
                else -> "unknown"
            }
            Log.e("ProfileActivity", "Problematic element: $problematicElement")

        }
    }

    private fun loadUserData() {
        Log.d("ProfileActivity", "=== loadUserData started ===")
        try {
            val name = sharedPreferences.getString("user_name", "Иван Иванов")
            val joinDate = sharedPreferences.getString("join_date", "Присоединился в июле 2024")
            val email = sharedPreferences.getString("user_email", "ivanov@mtuci.ru")

            Log.d("ProfileActivity", "Data: name=$name, joinDate=$joinDate, email=$email")

            tvUserName.text = name
            tvJoinDate.text = joinDate
            tvEmail.text = email

            loadAvatarFromStorage()

            Log.d("ProfileActivity", "=== User data loaded successfully ===")
        } catch (e: Exception) {
            Log.e("ProfileActivity", "ERROR in loadUserData: ${e.message}", e)
        }
    }

    private fun loadAvatarFromStorage() {
        val avatarFile = File(filesDir, "user_avatar.jpg")
        if (avatarFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(avatarFile.absolutePath)
            ivAvatar.setImageBitmap(bitmap)
        }
    }

    private fun saveAvatarToStorage(bitmap: Bitmap) {
        try {
            val avatarFile = File(filesDir, "user_avatar.jpg")
            val outputStream = FileOutputStream(avatarFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()

            val editor = sharedPreferences.edit()
            editor.putBoolean("has_avatar", true)
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupClickListeners() {
        Log.d("ProfileActivity", "=== setupClickListeners started ===")

        tvChangeAvatar.setOnClickListener {
            Log.d("ProfileActivity", "Change avatar clicked")
            showAvatarSelectionDialog()
        }

        tvChangePassword.setOnClickListener {
            Log.d("ProfileActivity", "Change password clicked")
            Toast.makeText(this, "Смена пароля???", Toast.LENGTH_SHORT).show()
        }

        tLogout.setOnClickListener {
            Log.d("ProfileActivity", "Logout clicked")
            clearUserData()
        }

        Log.d("ProfileActivity", "=== Click listeners set up ===")
    }

    private fun showAvatarSelectionDialog() {
        val options = arrayOf("Сделать фото", "Выбрать из галереи", "Отмена")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Изменить фото профиля")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CAMERA
            )
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_GALLERY
            )
        } else {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Для использования камеры нужны разрешения", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_REQUEST_GALLERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(this, "Для выбора фото нужны разрешения", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    val photo = data?.extras?.get("data") as? Bitmap
                    photo?.let {
                        ivAvatar.setImageBitmap(it)
                        saveAvatarToStorage(it)
                        Toast.makeText(this, "Фото обновлено", Toast.LENGTH_SHORT).show()
                    }
                }
                PICK_IMAGE_REQUEST -> {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let { uri ->
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                            ivAvatar.setImageBitmap(bitmap)
                            saveAvatarToStorage(bitmap)
                            Toast.makeText(this, "Фото обновлено", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this, "Ошибка загрузки фото", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun clearUserData() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Выход из профиля")
            .setMessage("Вы уверены, что хотите выйти?")
            .setPositiveButton("Выйти") { dialog, _ ->
                performLogout()
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogout() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", false)
        editor.remove("user_email")
        editor.remove("user_name")
        editor.apply()

        val avatarFile = File(filesDir, "user_avatar.jpg")
        if (avatarFile.exists()) {
            avatarFile.delete()
        }

        Toast.makeText(this, "Вы вышли из профиля", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }
}