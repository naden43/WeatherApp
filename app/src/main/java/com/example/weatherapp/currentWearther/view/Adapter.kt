package com.example.weatherapp.currentWearther.view


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherapp.databinding.HourItemBinding
import com.example.weatherapp.model.Data
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DayWeatherAdapter(var context: Context ) : ListAdapter<Data, ViewHolder>(DayWeatherDiffUtil()){

    lateinit var binding: HourItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourItemBinding.inflate(inflater , parent , false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = getItem(position)

        val dateTime = LocalDateTime.parse(currentObj.dt_txt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val formattedDate = dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
        if(currentObj.currentTime){
            holder.binding.txtTime.text = "Now"
        }
        else
        {
            holder.binding.txtTime.text = formattedDate
        }
        holder.binding.txtTemp.text = currentObj.main.temp.toInt().toString()
        Glide.with(context).load("https://openweathermap.org/img/wn/${currentObj.weather.get(0).icon}@4x.png")
            .apply(RequestOptions().override(200, 200)).into(holder.binding.conditionImage)
    }


}

data class ViewHolder(var binding: HourItemBinding): RecyclerView.ViewHolder(binding.root)

class DayWeatherDiffUtil : DiffUtil.ItemCallback<Data>(){
    override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem.currentTime==newItem.currentTime
    }

    override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem == newItem
    }
}




