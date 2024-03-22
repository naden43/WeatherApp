package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow

interface IRepository {
    fun getWeather(lat: Double, lon: Double): Flow<DayWeather>
    fun getWeather(lat: Double, lon: Double , lang: String): Flow<DayWeather>
    fun getWeather(lat: Double, lon: Double , lang: String , unit: String): Flow<DayWeather>
    fun setLanguage(lang: String)
    fun setUnit(unit: String)
    fun setWindSpeed(wind: String)
    fun setLocationMethod(location: String)
    fun getLanguage(): String
    fun getUnit(): String
    fun getWindSpeed(): String
    fun getLocationMethod(): String

    fun getLongitude():Double

    fun getLatitude() : Double

    fun setLatitude(lat:Double)

    fun setLongitude(lon:Double)

    fun insertDayForecast(dayWeather: DayWeather)

    fun deleteDayForecast(dayWeather: DayWeather)

    fun getDayForecast(lang: String) : Flow<DayWeather>

    fun getSession() : Boolean

    fun setSession(status:Boolean)
    fun getAllDayWeather(): Flow<List<DayWeather>>
    fun deleteAllDayWeather()
}