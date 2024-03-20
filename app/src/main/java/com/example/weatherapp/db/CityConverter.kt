package com.example.weatherapp.db

import com.example.weatherapp.model.City
import androidx.room.TypeConverter
import com.example.weatherapp.model.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type



class CityConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromCity(value: String?): City? {
        return gson.fromJson(value, City::class.java)
    }

    @TypeConverter
    fun fromCity(city: City?): String? {
        return gson.toJson(city)
    }
}