package com.example.weatherapp.network

import com.example.weatherapp.model.DayWeather
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WeatherRemoteDataSource {

     fun getWeather(lat:Double , lon:Double ): Flow<DayWeather>
     fun getWeather(lat:Double , lon:Double , lang:String): Flow<DayWeather>
     fun getWeather(lat:Double , lon:Double , lang:String , unit:String): Flow<DayWeather>


}