package com.example.drive.ui.Home.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drive.data.model.Car
import com.example.drive.databinding.ItemCarBinding
import com.example.drive.R

class CarAdapter(
    private val onBookClick: (Car) -> Unit,
    private val onDetailsClick: (Car) -> Unit
) : ListAdapter<Car, CarAdapter.CarViewHolder>(CarDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CarViewHolder(
        private val binding: ItemCarBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car) {
            with(binding) {

                // Загрузка изображения из ресурсов
                if (car.imageResId != 0) {
                    Glide.with(root.context)
                        .load(car.imageResId)
                        .centerCrop()
                        .into(carImageView)
                }

                brandTextView.text = car.brand
                modelTextView.text = car.model
                priceTextView.text = car.getPriceFormatted()
                specsTextView.text = "${car.transmission} • ${car.fuelType}"


                // Кнопки
                bookButton.setOnClickListener { onBookClick(car) }
                detailsButton.setOnClickListener { onDetailsClick(car) }
            }
        }
    }
}

class CarDiffCallback : DiffUtil.ItemCallback<Car>() {
    override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean {
        return oldItem == newItem
    }
}