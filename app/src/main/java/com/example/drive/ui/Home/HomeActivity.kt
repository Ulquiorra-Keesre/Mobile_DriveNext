package com.example.drive.ui.Home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drive.databinding.ActivityHomeBinding
import com.example.drive.ui.Home.HomeViewModel
import com.example.drive.ui.Home.Adapter.CarAdapter
import com.example.drive.ui.car.CarDetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.drive.R
import com.example.drive.ui.Settings.SettingsActivity


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var carAdapter: CarAdapter

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = binding.bottomNavigation

        setupRecyclerView()
        setupObservers()
        setupBottomNavigation()
        setupSearch()

    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter(
            onBookClick = { car ->
                // Бронирование автомобиля
                bookCar(car.id)
            },
            onDetailsClick = { car ->
                // Переход к деталям автомобиля
                val intent = Intent(this, CarDetailActivity::class.java)
                intent.putExtra("car_id", car.id)
                startActivity(intent)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = carAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        // Наблюдаем за списком автомобилей
        lifecycleScope.launch {
            viewModel.cars.collectLatest { cars ->
                carAdapter.submitList(cars)

                // Показываем/скрываем сообщение о пустом списке
                if (cars.isEmpty()) {
                    binding.errorTextView.text = "Автомобили не найдены"
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.errorTextView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }
        }

        // Наблюдаем за состоянием загрузки
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                if (isLoading) {
                    binding.errorTextView.visibility = View.GONE
                }
            }
        }

        // Наблюдаем за ошибками
        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    binding.errorTextView.text = it
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } ?: run {
                    binding.errorTextView.visibility = View.GONE
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navigation_bookings -> {
                    // TODO: Переход на BookingsActivity
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_home -> {
                    // Уже в HomeActivity
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSearch() {
        // Если есть поисковая строка
        binding.searchEditText?.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchCars(query)
            }
            false
        }
    }

    private fun bookCar(carId: Int) {
        lifecycleScope.launch {
            val success = viewModel.bookCar(carId)
            if (success) {
                // Показать уведомление об успешном бронировании
                // Можно использовать Snackbar или Toast
            } else {
                // Показать ошибку
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.navigation_home
    }
}