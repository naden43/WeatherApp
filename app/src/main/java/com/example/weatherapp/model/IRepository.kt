package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow

interface IRepository {
    fun getWeather(lat: Double, lon: Double): Flow<DayWeather>
}