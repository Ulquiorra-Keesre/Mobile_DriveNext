package com.example.drive.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.drive.data.local.dao.CarDao
import com.example.drive.data.local.dao.UserDao
import com.example.drive.data.model.Car
import com.example.drive.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import com.example.drive.R

@Database(
    entities = [Car::class, User::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carDao(): CarDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "car_rental_db"
                )
                    .fallbackToDestructiveMigration() // ← ДОБАВЬТЕ ЭТУ СТРОКУ!
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Заполняем базу начальными данными
                            Executors.newSingleThreadExecutor().execute {
                                getDatabase(context).apply {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        populateInitialData()
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData() {
            val db = INSTANCE ?: return

            db.carDao().deleteAllCars()
            db.userDao().deleteAllUsers()

            val testUser = User(
                id = 1,
                name = "Иван Иванов",
                email = "ivan@example.com",
                phone = "+7 (999) 123-45-67",
                avatarResId = 0
            )
            db.userDao().insertUser(testUser)

            val cars = listOf(
                Car(
                    id = 0,
                    brand = "Ferrari",
                    model = "Camry",
                    pricePerDay = 2500.0,
                    year = 2022,
                    fuelType = "Бензин",
                    transmission = "Автомат",
                    imageResId = R.drawable.car_ferrari,
                    description = "Комфортный седан бизнес-класса",
                    mileage = 300,
                    address = "Москва, ул. Тверская, 1",
                    photoUrl = ""
                ),
                Car(
                    id = 0,
                    brand = "Porshe",
                    model = "X5",
                    pricePerDay = 5000.0,
                    year = 2023,
                    fuelType = "Дизель",
                    transmission = "Автомат",
                    imageResId = R.drawable.car_porsche,
                    description = "Премиальный внедорожник",
                    mileage = 200,
                    address = "Санкт-Петербург, Невский пр., 10",
                    photoUrl = ""
                ),
                Car(
                    id = 0,
                    brand = "Posche",
                    model = "C-Class",
                    pricePerDay = 4000.0,
                    year = 2021,
                    fuelType = "Бензин",
                    transmission = "Автомат",
                    imageResId = R.drawable.car_porsche,
                    description = "Элегантный бизнес-седан",
                    mileage = 100,
                    address = "Казань, ул. Баумана, 5",
                    photoUrl = ""
                ),
                Car(
                    id = 0,
                    brand = "Ferrari",
                    model = "A4",
                    pricePerDay = 3500.0,
                    year = 2022,
                    fuelType = "Бензин",
                    transmission = "Автомат",
                    imageResId = R.drawable.car_ferrari,
                    description = "Спортивный седан",
                    mileage = 500,
                    address = "Екатеринбург, пр. Ленина, 20",
                    photoUrl = ""
                ),
                Car(
                    id = 0,
                    brand = "Ferrari",
                    model = "Rio",
                    pricePerDay = 1800.0,
                    year = 2021,
                    fuelType = "Бензин",
                    transmission = "Механика",
                    imageResId = R.drawable.car_ferrari,
                    description = "Экономичный городской автомобиль",
                    mileage = 350,
                    address = "Новосибирск, Красный пр., 15",
                    photoUrl = ""
                )
            )

            db.carDao().insertAllCars(cars)
        }
    }
}