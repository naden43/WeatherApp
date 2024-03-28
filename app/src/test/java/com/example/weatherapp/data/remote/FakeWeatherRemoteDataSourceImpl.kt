package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.model.FavouriteWeather
import kotlinx.coroutines.flow.Flow

class FakeWeatherRemoteDataSourceImpl : WeatherRemoteDataSource {
    override fun getWeather(lat: Double, lon: Double): Flow<DayWeather> {
        TODO("Not yet implemented")
    }

    override fun getWeather(lat: Double, lon: Double, lang: String): Flow<DayWeather> {
        TODO("Not yet implemented")
    }

    override fun getWeather(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<DayWeather> {
        TODO("Not yet implemented")
    }

}