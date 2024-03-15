package com.example.weatherapp.model

import com.example.weatherapp.network.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class Repository {

    lateinit var remoteWeather: WeatherRemoteDataSourceImpl

    private constructor(){
        remoteWeather = WeatherRemoteDataSourceImpl.Instance()
    }

    companion object{
        var INSTANCE:Repository? = null

        fun Instance():Repository {
            return INSTANCE ?: synchronized(this) {
                val tempInstance = Repository()
                INSTANCE = tempInstance
                tempInstance
            }
        }
    }

     fun getWeather(lat:Double , lon:Double): Flow<DayWeather> {
        return remoteWeather.getWeather(lat , lon)
    }


}