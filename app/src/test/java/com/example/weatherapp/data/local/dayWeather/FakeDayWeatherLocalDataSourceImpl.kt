package com.example.weatherapp.data.local.dayWeather

import com.example.weatherapp.data.model.AlertWeather
import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.model.FavouriteWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeDayWeatherLocalDataSourceImpl(private var weatherList:MutableList<DayWeather> = mutableListOf()): DayWeatherLocalDataSource {
    override fun insertDayForecast(dayWeather: DayWeather) {
        weatherList.add(dayWeather)
    }

    override fun deleteDayForecast(dayWeather: DayWeather) {
        TODO("Not yet implemented")
    }

    override fun getDayForecast(lang: String): Flow<DayWeather> {
        for(item in weatherList)
        {
            if(item.lang.equals(lang))
            {
                return flow {
                    emit(item)
                }
            }
        }

        return flow {
            emit(DayWeather())
        }
    }

    override fun getAllDayForecast(): Flow<List<DayWeather>> {

        return flow {
            emit(weatherList)
        }
    }

    override fun deleteAllDays() {
        weatherList.clear()
    }

    override fun insertFavourite(favouriteWeather: FavouriteWeather) {
        TODO("Not yet implemented")
    }

    override fun deleteFavourite(favouriteWeather: FavouriteWeather) {
        TODO("Not yet implemented")
    }

    override fun getFavourite(lon: Double, lat: Double): Flow<FavouriteWeather> {
        TODO("Not yet implemented")
    }

    override fun getFavourites(): Flow<List<FavouriteWeather>> {
        TODO("Not yet implemented")
    }

    override fun getAlerts(): Flow<List<AlertWeather>> {
        TODO("Not yet implemented")
    }

    override fun insertAlert(alertWeather: AlertWeather) {
        TODO("Not yet implemented")
    }

    override fun deleteAlert(alertWeather: AlertWeather) {
        TODO("Not yet implemented")
    }
}