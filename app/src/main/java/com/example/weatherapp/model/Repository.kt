package com.example.weatherapp.model

import com.example.weatherapp.network.WeatherRemoteDataSource
import com.example.weatherapp.network.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class Repository (var remoteWeather: WeatherRemoteDataSource ,  var localSettings:SettingLocalDataSource) : IRepository{



    companion object{
        var INSTANCE:Repository? = null

        fun Instance(remoteWeather: WeatherRemoteDataSource, localSettings:SettingLocalDataSource):Repository {
            return INSTANCE ?: synchronized(this) {
                val tempInstance = Repository(remoteWeather , localSettings)
                INSTANCE = tempInstance
                tempInstance
            }
        }
    }

     override fun getWeather(lat:Double, lon:Double): Flow<DayWeather> {
        return remoteWeather.getWeather(lat , lon)
     }

    override fun getWeather(lat:Double, lon:Double , lang:String): Flow<DayWeather> {
        return remoteWeather.getWeather(lat , lon , lang)
    }

    override fun getWeather(lat:Double, lon:Double , lang:String , unit:String): Flow<DayWeather> {
        return remoteWeather.getWeather(lat , lon , lang , unit)
    }

    override fun setLanguage(lang:String){
        localSettings.setLanguage(lang)
    }

    override fun setUnit(unit:String)
    {
        localSettings.setUnit(unit)
    }

    override fun setWindSpeed(wind:String)
    {
        localSettings.setWindSpeed(wind)
    }

    override fun setLocationMethod(location:String)
    {
        localSettings.setLocationMethod(location)
    }

    override fun getLanguage():String{
        return localSettings.getLanguage()
    }

    override fun getUnit():String{
        return localSettings.getUnit()
    }

    override fun getWindSpeed():String{
        return localSettings.getWindSpeed()
    }

    override fun getLocationMethod() : String{
        return localSettings.getLocationMethod()
    }

}