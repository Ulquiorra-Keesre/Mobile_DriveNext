package com.example.drive.ui.Home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drive.databinding.ActivityHomeBinding
import com.example.drive.ui.car.CarDetailActivity
import com.example.drive.ui.Search.SearchResultsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.drive.R
import com.example.drive.ui.Settings.SettingsActivity
import com.example.drive.ui.Home.Adapter.CarAdapter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var carAdapter: CarAdapter
    private var currentQuery: String = ""

    // View для состояний
    private lateinit var loadingView: View
    private lateinit var errorView: View

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализируем View для состояний
        initStateViews()

        setupRecyclerView()
        setupSearchView()
        setupBottomNavigation()
        setupObservers()

        // Загружаем данные при запуске
        viewModel.loadCars()
    }

    private fun initStateViews() {
        // Загрузка View для состояния загрузки
        loadingView = LayoutInflater.from(this).inflate(R.layout.loading, null)

        // Загрузка View для состояния ошибки
        errorView = LayoutInflater.from(this).inflate(R.layout.error, null)

        // Настройка кнопки повтора в состоянии ошибки
        errorView.findViewById<View>(R.id.retryButton).setOnClickListener {
            viewModel.loadCars()
        }

        // Скрываем все состояния при старте
        loadingView.visibility = View.GONE
        errorView.visibility = View.GONE
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

                // Показываем основной контент
                showContentState()

                if (cars.isEmpty()) {
                    if (currentQuery.isNotEmpty()) {
                        binding.errorTextView.text = "По запросу \"$currentQuery\" ничего не найдено"
                    } else {
                        binding.errorTextView.text = "Автомобили не найдены"
                    }
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.errorTextView.visibility = View.GONE
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
                }
            }
        }
    }

    private fun showLoadingState() {
        // Скрываем основной контент
        binding.recyclerView.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
        binding.searchView.visibility = View.GONE

        // Показываем экран загрузки
        if (!isViewAdded(loadingView)) {
            binding.root.addView(loadingView)
        }
        loadingView.visibility = View.VISIBLE
        errorView.visibility = View.GONE
    }

    private fun showErrorState(errorMessage: String) {
        // Скрываем основной контент
        binding.recyclerView.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
        binding.searchView.visibility = View.GONE

        // Настраиваем сообщение об ошибке
        errorView.findViewById<android.widget.TextView>(R.id.errorMessage).text = errorMessage

        // Показываем экран ошибки
        if (!isViewAdded(errorView)) {
            binding.root.addView(errorView)
        }
        errorView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
    }

    private fun showContentState() {
        // Скрываем состояния
        if (isViewAdded(loadingView)) loadingView.visibility = View.GONE
        if (isViewAdded(errorView)) errorView.visibility = View.GONE

        // Показываем основной контент
        binding.recyclerView.visibility = View.VISIBLE
        binding.searchView.visibility = View.VISIBLE
    }

    private fun isViewAdded(view: View): Boolean {
        return view.parent != null
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