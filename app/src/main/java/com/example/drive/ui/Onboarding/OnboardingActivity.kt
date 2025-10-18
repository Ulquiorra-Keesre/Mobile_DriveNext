package com.example.drive.ui.Onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.drive.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.drive.ui.Onboarding.OnboardingAdapter
import com.example.drive.ui.LoginActivity
import com.example.drive.ui.Onboarding.Slide
import androidx.core.content.ContextCompat

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnSkip: Button
    private lateinit var btnNext: Button
    private lateinit var btnGetStarted: Button

    private val slides = listOf<Slide>(
        Slide("Аренда автомобилей", "Открой для себя удобный и доступный способ передвижения", R.drawable.onboarding_1),
        Slide("Безопасно и удобно", "Арендуй автомобиль и наслаждайся его удобством", R.drawable.onboarding_2),
        Slide("Лучшие предложения", "Выбирай понравившееся среди сотен доступных автомобилей", R.drawable.onboarding_3)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        initViews()
        setupViewPager()
        setupClickListeners()

        // Скрыть "Поехали" изначально
        btnGetStarted.visibility = View.GONE
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)
        btnGetStarted = findViewById(R.id.btnGetStarted)
    }

    private fun setupViewPager() {
        val adapter = OnboardingAdapter(slides)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Не показываем текст, только индикатор
            tab.view.background = if (position == viewPager.currentItem) {
                ContextCompat.getDrawable(this, R.drawable.tab_selected)
            } else {
                ContextCompat.getDrawable(this, R.drawable.tab_unselected)
            }
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Перерисовываем все табы при смене страницы
                for (i in 0 until tabLayout.tabCount) {
                    val tab = tabLayout.getTabAt(i)
                    tab?.view?.background = if (i == position) {
                        ContextCompat.getDrawable(this@OnboardingActivity, R.drawable.tab_selected)
                    } else {
                        ContextCompat.getDrawable(this@OnboardingActivity, R.drawable.tab_unselected)
                    }
                }

                when (position) {
                    slides.lastIndex -> {
                        btnSkip.visibility = View.VISIBLE
                        btnNext.visibility = View.GONE
                        btnGetStarted.visibility = View.VISIBLE
                    }
                    else -> {
                        btnSkip.visibility = View.VISIBLE
                        btnNext.visibility = View.VISIBLE
                        btnGetStarted.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun setupClickListeners() {
        btnSkip.setOnClickListener {
            finishOnboarding()
        }

        btnNext.setOnClickListener {
            if (viewPager.currentItem < slides.lastIndex) {
                viewPager.currentItem += 1
            }
        }

        btnGetStarted.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        // Сохраняем, что onboarding пройден
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_completed", true).apply()

        // Переход к LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}