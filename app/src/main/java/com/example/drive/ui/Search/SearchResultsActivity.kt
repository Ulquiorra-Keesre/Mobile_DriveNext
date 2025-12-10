package com.example.drive.ui.Search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drive.DriveApp
import com.example.drive.databinding.ActivitySearchResultsBinding
import com.example.drive.ui.home.HomeViewModel
import com.example.drive.ui.home.HomeViewModelFactory
import com.example.drive.ui.home.adapter.CarAdapter

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultsBinding
    private lateinit var adapter: CarAdapter

    // Используем тот же ViewModel, что и на главном экране
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(application as DriveApp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupObservers()

        // Получаем поисковый запрос из Intent
        val query = intent.getStringExtra("search_query") ?: ""
        if (query.isNotEmpty()) {
            binding.searchView.setQuery(query, false)
            viewModel.searchCars(query)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Результаты поиска"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = CarAdapter(
            onBookClick = { car ->
                // TODO: Переход на бронирование
                showMessage("Бронирование ${car.brand} ${car.model}")
            },
            onDetailsClick = { car ->
                // TODO: Переход на детали
                showMessage("Детали ${car.brand} ${car.model}")
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchResultsActivity)
            adapter = this@SearchResultsActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    performSearch("")
                }
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        viewModel.searchCars(query)
    }

    private fun setupObservers() {
        viewModel.cars.observe(this) { cars ->
            adapter.submitList(cars)

            // Обновляем статус
            binding.searchStatusTextView.text = when {
                cars.isEmpty() -> "Ничего не найдено"
                else -> "Найдено ${cars.size} автомобилей"
            }

            // Показываем/скрываем сообщение о пустом результате
            if (cars.isEmpty()) {
                binding.noResultsLayout.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.noResultsLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun start(context: android.content.Context, query: String) {
            val intent = Intent(context, SearchResultsActivity::class.java)
            intent.putExtra("search_query", query)
            context.startActivity(intent)
        }
    }
}