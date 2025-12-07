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
    version = 1,
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
                    id = 1,
                    brand = "Toyota",
                    model = "Camry",
                    pricePerDay = 2500.0,
                    year = 2022,
                    fuelType = "Бензин",
                    transmission = "Автомат",
                    seats = 5,
                    engineVolume = "2.5 л",
                    power = 203,
                    color = "Черный",
                    imageResId = R.drawable.car_ferrari,
                    rating = 4.7f,
                    description = "Комфортный седан бизнес-класса"
                ),
                Car(
                    id = 2,
                    brand = "BMW",
                    model = "X5",
                    pricePerDay = 5000.0,
                    year = 2023,
                    fuelType = "Дизель",
                    transmission = "Автомат",
                    seats = 5,
                    engineVolume = "3.0 л",
                    power = 286,
                    color = "Белый",
                    imageResId = R.drawable.car_porsche,
                    rating = 4.9f,
                    description = "Премиальный внедорожник"
                ),
                Car(
                    id = 3,
                    brand = "Mercedes",
                    model = "C-Class",
                    pricePerDay = 4000.0,
                    year = 2021,
                    fuelType = "Бензин",
                    transmission = "Автомат",
                    seats = 5,
                    engineVolume = "2.0 л",
                    power = 258,
                    color = "Серый",
                    imageResId = R.drawable.car_porsche,
                    rating = 4.8f,
                    description = "Элегантный бизнес-седан"
                ),
                Car(
                    id = 4,
                    brand = "Audi",
                    model = "A4",
                    pricePerDay = 3500.0,
                    year = 2022,
                    fuelType = "Бензин",
                    transmission = "Автомат",
                    seats = 5,
                    engineVolume = "2.0 л",
                    power = 190,
                    color = "Синий",
                    imageResId = R.drawable.car_ferrari,
                    rating = 4.6f,
                    description = "Спортивный седан"
                ),
                Car(
                    id = 5,
                    brand = "Kia",
                    model = "Rio",
                    pricePerDay = 1800.0,
                    year = 2021,
                    fuelType = "Бензин",
                    transmission = "Механика",
                    seats = 5,
                    engineVolume = "1.6 л",
                    power = 123,
                    color = "Красный",
                    imageResId = R.drawable.car_ferrari,
                    rating = 4.3f,
                    description = "Экономичный городской автомобиль"
                )
            )

            db.carDao().insertAllCars(cars)
        }
    }
}