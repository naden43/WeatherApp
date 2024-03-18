package com.example.weatherapp.network

import com.example.weatherapp.model.DayWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("forecast")
    suspend fun getWeather(@Query("lat") lat:Double , @Query("lon") lon:Double  , @Query("appid") appid:String):Response<DayWeather>

    @GET("forecast")
    suspend fun getWeather(@Query("lat") lat:Double , @Query("lon") lon:Double  ,@Query("lang") lang:String ,@Query("appid") appid:String):Response<DayWeather>

    @GET("forecast")
    suspend fun getWeather(@Query("lat") lat:Double , @Query("lon") lon:Double  ,@Query("lang") lang:String , @Query("units") unit:String ,@Query("appid") appid:String):Response<DayWeather>


}