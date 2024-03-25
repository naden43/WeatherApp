package com.example.weatherapp.network

import com.example.weatherapp.model.DayWeather
import com.example.weatherapp.model.FavouriteWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRemoteDataSourceImpl : WeatherRemoteDataSource {


     val BASE_URL = "https://api.openweathermap.org/data/2.5/"
     lateinit var retrofit: Retrofit
     lateinit var api:ApiService

    val appid:String = "936e138c87bad11b4cc706b7849cf427"


    private constructor(){

         retrofit =  Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(
             BASE_URL
         ).build()
         api = retrofit.create(ApiService::class.java);
     }

    companion object{
        var instance:WeatherRemoteDataSourceImpl? = null

        fun Instance() : WeatherRemoteDataSourceImpl {
            return instance?:synchronized(this){
                val tempInstance = WeatherRemoteDataSourceImpl()
                instance = tempInstance
                tempInstance
            }
        }
    }


    override  fun getWeather(lat:Double , lon:Double): Flow<DayWeather>{
        return flow<DayWeather> {
            emit(api.getWeather(lat , lon , appid).body()!!)
        }
    }

    override fun getWeather(lat: Double, lon: Double, lang: String): Flow<DayWeather> {
        return flow<DayWeather> {
            emit(api.getWeather(lat , lon , lang ,appid).body()!!)
        }
    }
    override fun getWeather(lat: Double, lon: Double, lang: String , unit:String): Flow<DayWeather> {
        return flow<DayWeather> {
            emit(api.getWeather(lat , lon , lang , unit, appid).body()!!)
        }
    }

    override fun getFavWeather(lat: Double, lon: Double, lang: String): Flow<FavouriteWeather> {
        return flow<FavouriteWeather> {
            emit(api.getFavWeather(lat , lon , lang , appid).body()!!)
        }
    }




}