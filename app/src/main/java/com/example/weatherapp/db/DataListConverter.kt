package com.example.weatherapp.db

import androidx.room.TypeConverter
import com.example.weatherapp.data.model.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class DataListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<Data?>? {
        val listType: Type = object : TypeToken<List<Data?>?>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Data?>?): String? {
        return gson.toJson(list)
    }

}