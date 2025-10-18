package com.example.drive.ui.Onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.drive.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.drive.ui.LoginActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    private lateinit var btnSkip: Button
    private lateinit var btnNext: Button
    private lateinit var btnGetStarted: Button

    private lateinit var indicator1: View
    private lateinit var indicator2: View
    private lateinit var indicator3: View

    private val slides = listOf(
        Slide(
            title = "Аренда автомобилей",
            description = "Открой для себя удобный и доступный способ передвижения",
            imageRes = R.drawable.onboarding_1
        ),
        Slide(
            title = "Безопасно и удобно",
            description = "Арендуй автомобиль и наслаждайся его удобством",
            imageRes = R.drawable.onboarding_2
        ),
        Slide(
            title = "Лучшие предложения",
            description = "Выбирай понравившееся среди сотен доступных автомобилей",
            imageRes = R.drawable.onboarding_3
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        initIndicators()
        initViews()
        setupViewPager()
        setupClickListeners()

        btnGetStarted.visibility = View.GONE
    }

    private fun initIndicators() {
        indicator1 = findViewById(R.id.indicator1)
        indicator2 = findViewById(R.id.indicator2)
        indicator3 = findViewById(R.id.indicator3)
    }

    private fun updateIndicators(position: Int) {
        indicator1.background = ContextCompat.getDrawable(this, if (position == 0) R.drawable.tab_selected else R.drawable.tab_unselected)
        indicator2.background = ContextCompat.getDrawable(this, if (position == 1) R.drawable.tab_selected else R.drawable.tab_unselected)
        indicator3.background = ContextCompat.getDrawable(this, if (position == 2) R.drawable.tab_selected else R.drawable.tab_unselected)
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)

        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)
        btnGetStarted = findViewById(R.id.btnGetStarted)
    }

    private fun setupViewPager() {
        val adapter = OnboardingAdapter(slides)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)

                when (position) {
                    slides.lastIndex -> {
                        btnSkip.visibility = View.GONE
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
    } // ← ЭТА СКОБКА ОБЯЗАТЕЛЬНА!



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
        getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("onboarding_completed", true)
            .apply()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}