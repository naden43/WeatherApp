package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.model.FavouriteWeather
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {

     fun getWeather(lat:Double , lon:Double ): Flow<DayWeather>
     fun getWeather(lat:Double , lon:Double , lang:String): Flow<DayWeather>
     fun getWeather(lat:Double , lon:Double , lang:String , unit:String): Flow<DayWeather>


}