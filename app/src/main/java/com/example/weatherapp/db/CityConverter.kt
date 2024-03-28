package com.example.weatherapp.db

import com.example.weatherapp.data.model.City
import androidx.room.TypeConverter
import com.google.gson.Gson


class CityConverter {
    private val gson = Gson()

    @TypeConverter
    fun toCity(cityString: String): City {
        return gson.fromJson(cityString, City::class.java)
    }
    @TypeConverter
    fun fromCity(city: City): String {
        return gson.toJson(city)
    }


}