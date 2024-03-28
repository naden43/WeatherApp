package com.example.weatherapp.favourite.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.FavItemBinding
import com.example.weatherapp.data.model.FavouriteWeather

class FavouriteAdapter
    (var context: Context , var listener: (FavouriteWeather) -> Unit) : ListAdapter<FavouriteWeather, FavouriteViewHolder>(
    FavouriteWeatherDiffUtil()
){

    lateinit var binding: FavItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavItemBinding.inflate(inflater , parent , false)
        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val currentObj = getItem(position)

        Log.i("TAG", "onBindViewHolder: here")
        binding.countryNameTxt.text = currentObj.countryName

        binding.deleteBtn.setOnClickListener {
            listener(currentObj)
        }

        binding.itemFav.setOnClickListener {
            val action = FavouriteScreenDirections.actionFavouriteScreenToFavDetails()
            action.latitude = currentObj.lat.toString()
            action.longitude = currentObj.lon.toString()
            Navigation.findNavController(binding.root).navigate(action)
        }

    }
}

data class FavouriteViewHolder(var binding: FavItemBinding): RecyclerView.ViewHolder(binding.root)

class FavouriteWeatherDiffUtil : DiffUtil.ItemCallback<FavouriteWeather>(){
    override fun areItemsTheSame(oldItem: FavouriteWeather, newItem: FavouriteWeather): Boolean {
        return oldItem.lon==newItem.lon && oldItem.lat == newItem.lat
    }

    override fun areContentsTheSame(oldItem: FavouriteWeather, newItem: FavouriteWeather): Boolean {
        return oldItem == newItem
    }
}




