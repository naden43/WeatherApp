package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow

interface IRepository {
    fun getWeather(lat: Double, lon: Double): Flow<DayWeather>
    fun setLanguage(lang: String)
    fun setUnit(unit: String)
    fun setWindSpeed(wind: String)
    fun setLocationMethod(location: String)
    fun getLanguage(): String
    fun getUnit(): String
    fun getWindSpeed(): String
    fun getLocationMethod(): String
}