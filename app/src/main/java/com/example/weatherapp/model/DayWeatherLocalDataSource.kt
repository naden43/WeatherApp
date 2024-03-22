package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow


interface DayWeatherLocalDataSource {

    fun insertDayForecast(dayWeather: DayWeather)
    fun deleteDayForecast(dayWeather: DayWeather)

    fun getDayForecast(lang:String): Flow<DayWeather>


    fun getAllDayForecast(): Flow<List<DayWeather>>
    fun deleteAllDays()
}