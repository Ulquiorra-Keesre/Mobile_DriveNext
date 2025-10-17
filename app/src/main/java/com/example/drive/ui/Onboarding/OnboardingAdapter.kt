package com.example.drive.ui.Onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drive.R
import com.example.drive.ui.Onboarding.Slide

class OnboardingAdapter(private val slides: List<Slide>) :
    RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.O_image)
        val title: TextView = view.findViewById(R.id.O_title)
        val description: TextView = view.findViewById(R.id.O_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding_slide, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slide = slides[position]
        holder.image.setImageResource(slide.imageRes)
        holder.title.text = slide.title
        holder.description.text = slide.description
        println("Binding slide: ${slide.title}")
    }

    override fun getItemCount() = slides.size
}