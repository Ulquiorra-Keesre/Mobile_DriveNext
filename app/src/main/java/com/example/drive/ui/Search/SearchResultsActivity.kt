package com.example.drive.ui.Search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drive.databinding.ActivitySearchResultsBinding
import com.example.drive.ui.Home.HomeViewModel
import com.example.drive.ui.Home.HomeViewModelFactory
import com.example.drive.ui.Home.Adapter.CarAdapter
import com.example.drive.ui.car.CarDetailActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.drive.R

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultsBinding
    private lateinit var adapter: CarAdapter
    private var currentQuery: String = ""

    // Используем фабрику для ViewModel
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(application as com.example.drive.DriveApp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupRecyclerView()
        setupObservers()

        currentQuery = intent.getStringExtra("search_query") ?: ""
        if (currentQuery.isNotEmpty()) {
            performSearch(currentQuery)
        }
    }

    private fun setupHeader() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = CarAdapter(
            onBookClick = { car ->
                bookCar(car.id)
            },
            onDetailsClick = { car ->
                navigateToCarDetails(car.id)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchResultsActivity)
            adapter = this@SearchResultsActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            viewModel.loadCars()
        } else {
            viewModel.searchCars(query)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.cars.collectLatest { cars ->
                adapter.submitList(cars)

                updateSearchStatus(cars.size)

                if (cars.isEmpty()) {
                    binding.noResultsLayout.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.noResultsLayout.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }

                showContentState()
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
                    if (!viewModel.isLoading.value) {
                        showContentState()
                    }
                }
            }
        }
    }

    private fun updateSearchStatus(count: Int) {
        val countText = when (count) {
            0 -> "автомобилей"
            1 -> "автомобиль"
            in 2..4 -> "автомобиля"
            else -> "автомобилей"
        }

        binding.searchStatusTextView.text = when {
            currentQuery.isEmpty() && count == 0 -> "Автомобили не найдены"
            currentQuery.isEmpty() && count > 0 -> "Все автомобили ($count)"
            currentQuery.isNotEmpty() && count == 0 -> "По запросу \"$currentQuery\" ничего не найдено"
            currentQuery.isNotEmpty() && count > 0 -> "Найдено $count $countText"
            else -> "Результаты поиска"
        }
    }

    private fun showLoadingState() {
        binding.stateContainer.removeAllViews()

        val loadingView = LayoutInflater.from(this).inflate(R.layout.loading, null)
        binding.stateContainer.addView(loadingView)

        loadingView.findViewById<android.widget.TextView>(R.id.loadingText)?.text =
            if (currentQuery.isNotEmpty()) {
                "Ищем по запросу: \"$currentQuery\""
            } else {
                "Загружаем автомобили..."
            }

        // Kонтейнер состояний
        binding.stateContainer.visibility = View.VISIBLE

        // Oсновной контент
        binding.recyclerView.visibility = View.GONE
        binding.noResultsLayout.visibility = View.GONE
        binding.searchStatusTextView.visibility = View.GONE
        binding.headerLayout.visibility = View.GONE
    }

    private fun showErrorState(errorMessage: String) {
        binding.stateContainer.removeAllViews()

        val errorView = LayoutInflater.from(this).inflate(R.layout.error, null)
        binding.stateContainer.addView(errorView)

        errorView.findViewById<android.widget.TextView>(R.id.errorMessage).text = errorMessage

        errorView.findViewById<android.widget.Button>(R.id.retryButton).setOnClickListener {
            performSearch(currentQuery)
        }

        // Показываем контейнер состояний
        binding.stateContainer.visibility = View.VISIBLE

        // Скрываем основной контент
        binding.recyclerView.visibility = View.GONE
        binding.noResultsLayout.visibility = View.GONE
        binding.searchStatusTextView.visibility = View.GONE
        binding.headerLayout.visibility = View.GONE
    }

    private fun showContentState() {
        binding.stateContainer.visibility = View.GONE

        binding.recyclerView.visibility = View.VISIBLE
        binding.searchStatusTextView.visibility = View.VISIBLE
        binding.headerLayout.visibility = View.VISIBLE

        binding.progressBar.visibility = View.GONE
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

    companion object {
        fun start(context: android.content.Context, query: String) {
            val intent = Intent(context, SearchResultsActivity::class.java)
            intent.putExtra("search_query", query)
            context.startActivity(intent)
        }
    }
}