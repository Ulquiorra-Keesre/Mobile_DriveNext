package com.example.drive.ui.Home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drive.databinding.ActivityHomeBinding
import com.example.drive.ui.car.CarDetailActivity
import com.example.drive.ui.Search.SearchResultsActivity
import com.example.drive.ui.Settings.SettingsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.drive.R
import com.example.drive.ui.Home.Adapter.CarAdapter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var carAdapter: CarAdapter
    private var currentQuery: String = ""

    // Правильное создание ViewModel с фабрикой
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(application as com.example.drive.DriveApp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Быстрая проверка авторизации
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {
            // Если не авторизован, отправляем на логин
            val intent = Intent(this, com.example.drive.ui.Authentication_Registration.LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchView()
        setupBottomNavigation()
        setupObservers()

        // Загружаем данные при запуске
        viewModel.loadCars()
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter(
            onBookClick = { car ->
                bookCar(car.id)
            },
            onDetailsClick = { car ->
                navigateToCarDetails(car.id)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = carAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            queryHint = "Поиск автомобилей..."

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if (query.isNotEmpty()) {
                        openSearchResults(query)
                        clearFocus()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    currentQuery = newText
                    if (newText.isEmpty()) {
                        viewModel.loadCars()
                    } else if (newText.length >= 2) {
                        viewModel.searchCars(newText)
                    }
                    return true
                }
            })

            setOnCloseListener {
                currentQuery = ""
                viewModel.loadCars()
                false
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.cars.collectLatest { cars ->
                carAdapter.submitList(cars)

                // Показываем основной контент когда данные загружены
                showContentState()

                if (cars.isEmpty()) {
                    if (currentQuery.isNotEmpty()) {
                        // Показываем сообщение о пустом поиске
                        binding.noResultsLayout.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        // Настраиваем текст
                        binding.noResultsTitle.text = "По запросу \"$currentQuery\" ничего не найдено"
                        binding.noResultsSubtitle.text = "Попробуйте изменить запрос поиска"
                    } else {
                        // Показываем общее сообщение о пустом списке
                        binding.noResultsLayout.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.noResultsTitle.text = "Автомобили не найдены"
                        binding.noResultsSubtitle.text = "Попробуйте позже"
                    }
                } else {
                    binding.noResultsLayout.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                if (isLoading) {
                    showLoadingState()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    showErrorState(it)
                } ?: run {
                    // Если ошибка исчезла и не загружаем, показываем контент
                    if (!viewModel.isLoading.value) {
                        showContentState()
                    }
                }
            }
        }
    }

    private fun showLoadingState() {
        // Очищаем контейнер состояний
        binding.stateContainer.removeAllViews()

        // Загружаем и добавляем layout загрузки
        val loadingView = LayoutInflater.from(this).inflate(R.layout.loading, null)
        binding.stateContainer.addView(loadingView)

        // Настраиваем тексты
        loadingView.findViewById<TextView>(R.id.loadingText)?.text =
            if (currentQuery.isNotEmpty()) {
                "Ищем по запросу: \"$currentQuery\""
            } else {
                "Ищем подходящие автомобили"
            }

        // Показываем контейнер состояний
        binding.stateContainer.visibility = View.VISIBLE

        // Скрываем основной контент
        binding.recyclerView.visibility = View.GONE
        binding.noResultsLayout.visibility = View.GONE
        binding.searchView.visibility = View.GONE
        binding.searchTitle.visibility = View.GONE
        binding.bottomNavigation.visibility = View.GONE

        // Скрываем старые элементы
        binding.progressBar.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
    }

    private fun showErrorState(errorMessage: String) {
        // Очищаем контейнер состояний
        binding.stateContainer.removeAllViews()

        // Загружаем и добавляем layout ошибки
        val errorView = LayoutInflater.from(this).inflate(R.layout.error, null)
        binding.stateContainer.addView(errorView)

        // Настраиваем сообщение об ошибке
        errorView.findViewById<TextView>(R.id.errorMessage).text = errorMessage

        // Настраиваем кнопку повтора
        errorView.findViewById<android.widget.Button>(R.id.retryButton).setOnClickListener {
            if (currentQuery.isNotEmpty()) {
                viewModel.searchCars(currentQuery)
            } else {
                viewModel.loadCars()
            }
        }

        // Показываем контейнер состояний
        binding.stateContainer.visibility = View.VISIBLE

        // Скрываем основной контент
        binding.recyclerView.visibility = View.GONE
        binding.noResultsLayout.visibility = View.GONE
        binding.searchView.visibility = View.GONE
        binding.searchTitle.visibility = View.GONE
        binding.bottomNavigation.visibility = View.GONE

        // Скрываем старые элементы
        binding.progressBar.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
    }

    private fun showContentState() {
        // Скрываем контейнер состояний
        binding.stateContainer.visibility = View.GONE

        // Показываем основной контент
        binding.recyclerView.visibility = View.VISIBLE
        binding.searchView.visibility = View.VISIBLE
        binding.searchTitle.visibility = View.VISIBLE
        binding.bottomNavigation.visibility = View.VISIBLE

        // Скрываем старые элементы
        binding.progressBar.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
    }

    private fun openSearchResults(query: String) {
        SearchResultsActivity.start(this, query)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_home

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    true
                }
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
                else -> false
            }
        }
    }

    private fun navigateToCarDetails(carId: Int) {
        val intent = Intent(this, CarDetailActivity::class.java)
        intent.putExtra("car_id", carId)
        startActivity(intent)
    }

    private fun bookCar(carId: Int) {
        lifecycleScope.launch {
            val success = viewModel.bookCar(carId)
            val message = if (success) {
                "Автомобиль успешно забронирован!"
            } else {
                "Ошибка при бронировании. Попробуйте снова."
            }

            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = R.id.navigation_home

        if (currentQuery.isNotEmpty()) {
            viewModel.searchCars(currentQuery)
        } else {
            viewModel.loadCars()
        }
    }
}