package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow


interface DayWeatherLocalDataSource {

    fun insertDayForecast(dayWeather: DayWeather)
    fun deleteDayForecast(dayWeather: DayWeather)

    fun getDayForecast(lang:String): Flow<DayWeather>


    fun getAllDayForecast(): Flow<List<DayWeather>>
    fun deleteAllDays()
    fun insertFavourite(favouriteWeather: FavouriteWeather)
    fun deleteFavourite(favouriteWeather: FavouriteWeather)
    fun getFavourite(lon: Double, lat: Double): Flow<FavouriteWeather>
    fun getFavourites(): Flow<List<FavouriteWeather>>
    fun getAlerts(): Flow<List<AlertWeather>>
    fun insertAlert(alertWeather: AlertWeather)
    fun deleteAlert(alertWeather: AlertWeather)
}