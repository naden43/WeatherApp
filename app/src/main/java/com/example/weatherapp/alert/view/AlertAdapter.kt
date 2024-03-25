package com.example.weatherapp.alert.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.AlertItemBinding
import com.example.weatherapp.model.AlertWeather

class AlertAdapter  (var context: Context , var listener: (AlertWeather) -> Unit) : ListAdapter<AlertWeather, AlertViewHolder>(
    AlertWeatherDiffUtil()
){

    lateinit var binding: AlertItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertItemBinding.inflate(inflater , parent , false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val currentObj = getItem(position)

        binding.dateTxt.text = currentObj.date

        binding.timeTxt.text = currentObj.time

        binding.deleteAlert.setOnClickListener {
            listener(currentObj)
        }
    }
}

data class AlertViewHolder(var binding: AlertItemBinding): RecyclerView.ViewHolder(binding.root)

class AlertWeatherDiffUtil : DiffUtil.ItemCallback<AlertWeather>(){
    override fun areItemsTheSame(oldItem: AlertWeather, newItem: AlertWeather): Boolean {
        return oldItem.requestId==newItem.requestId
    }

    override fun areContentsTheSame(oldItem: AlertWeather, newItem: AlertWeather): Boolean {
        return oldItem == newItem
    }
}

