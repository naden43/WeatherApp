package com.example.weatherapp.model

import android.content.Context
import com.example.weatherapp.db.DayWeatherDao
import com.example.weatherapp.db.WeatherDataBase
import kotlinx.coroutines.flow.Flow

class DayWeatherLocalDataSourceImpl(context: Context) : DayWeatherLocalDataSource {


    lateinit var dayWeatherDao: DayWeatherDao
    lateinit var dataBase: WeatherDataBase
    companion object{
        @Volatile
        private var INSTANCE : DayWeatherLocalDataSourceImpl? = null

        fun getInstance(context: Context) : DayWeatherLocalDataSourceImpl
        {
            return INSTANCE?:synchronized(this){
                val instance = DayWeatherLocalDataSourceImpl(context)
                INSTANCE = instance
                instance
            }
        }

    }

    init {
        dataBase = WeatherDataBase.getInstance(context)
        dayWeatherDao = dataBase.getDayWeatherDao()
    }





    override fun insertDayForecast(dayWeather: DayWeather) {
           dayWeatherDao.insertDayForecast(dayWeather)
    }

    override fun deleteDayForecast(dayWeather: DayWeather) {
        dayWeatherDao.deleteDayForecast(dayWeather)
    }

    override fun getDayForecast(lang:String): Flow<DayWeather> {
        return dayWeatherDao.getDayWeather(lang)
    }


}