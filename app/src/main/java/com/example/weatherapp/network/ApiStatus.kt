package com.example.weatherapp.network

import com.example.weatherapp.model.DayWeather

sealed class ApiStatus<out T> {
    data class Success<T>(val data: T) : ApiStatus<T>()
    data class Failure(val message: Throwable) : ApiStatus<Nothing>()
    object Loading : ApiStatus<Nothing>()
}
