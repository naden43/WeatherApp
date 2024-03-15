package com.example.weatherapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherapp.databinding.DailyItemBinding
import com.example.weatherapp.model.Data
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DailyWeatherAdapter(var context: Context) : ListAdapter<Data, DailyViewHolder>(DailyWeatherDiffUtil()){

    lateinit var binding: DailyItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DailyItemBinding.inflate(inflater , parent , false)
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item , parent , false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val currentObj = getItem(position)

        val dateTime = LocalDateTime.parse(currentObj.dt_txt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        // Get day of the week abbreviated (e.g., "Mon", "Tue")
        val day = dateTime.format(DateTimeFormatter.ofPattern("E"))

        holder.binding.dayTxt.text = day
        holder.binding.lowTemp.text= currentObj.main.temp_min.toString()
        holder.binding.highTemp.text = currentObj.main.temp_max.toString()

        Glide.with(context).load("https://openweathermap.org/img/wn/${currentObj.weather.get(0).icon}@4x.png")
            .apply(RequestOptions().override(200, 200)).into(holder.binding.dayImage)
    }
}

data class DailyViewHolder(var binding: DailyItemBinding): RecyclerView.ViewHolder(binding.root)

class DailyWeatherDiffUtil : DiffUtil.ItemCallback<Data>(){
    override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem.dt_txt==newItem.dt_txt
    }

    override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem == newItem
    }
}




