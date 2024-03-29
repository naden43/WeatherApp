package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.dayWeather.DayWeatherLocalDataSource
import com.example.weatherapp.data.local.setting.SettingLocalDataSource
import com.example.weatherapp.data.model.AlertWeather
import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.model.FavouriteWeather
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class Repository (var remoteWeather: WeatherRemoteDataSource, var localSettings: SettingLocalDataSource, var localWeather: DayWeatherLocalDataSource) :
    IRepository {



    companion object{
        var INSTANCE: Repository? = null

        fun Instance(remoteWeather: WeatherRemoteDataSource, localSettings: SettingLocalDataSource, localWeather: DayWeatherLocalDataSource): Repository {
            return INSTANCE ?: synchronized(this) {
                val tempInstance = Repository(remoteWeather , localSettings , localWeather)
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

    override fun getLongitude(): Double {
        return localSettings.getLongitude()
    }

    override fun getLatitude(): Double {
        return localSettings.getLatitude()
    }

    override fun setLatitude(lat: Double) {
        localSettings.setLatitude(lat)
    }

    override fun setLongitude(lon: Double) {
        localSettings.setLongitude(lon)
    }

    override fun insertDayForecast(dayWeather: DayWeather) {
        localWeather.insertDayForecast(dayWeather)
    }

    override fun deleteDayForecast(dayWeather: DayWeather) {
        localWeather.deleteDayForecast(dayWeather)
    }

    override fun getDayForecast(lang: String): Flow<DayWeather> {
         return localWeather.getDayForecast(lang)
    }

    override fun getSession(): Boolean {
        return localSettings.getSession()
    }

    override fun setSession(status: Boolean) {
        localSettings.setSession(status)
    }

    override fun getAllDayWeather()  : Flow<List<DayWeather>>{
       return localWeather.getAllDayForecast()
    }

    override fun deleteAllDayWeather() {
        localWeather.deleteAllDays()
    }


    override fun insertFavourite(favouriteWeather: FavouriteWeather){
        localWeather.insertFavourite(favouriteWeather)
    }

    override fun deleteFavourite(favouriteWeather: FavouriteWeather){
        localWeather.deleteFavourite(favouriteWeather)
    }

    override fun getFavourite(lon:Double , lat:Double) : Flow<FavouriteWeather>{
        return localWeather.getFavourite(lon , lat)
    }


    override fun getFavourites() : Flow<List<FavouriteWeather>>{
        return localWeather.getFavourites()
    }

    override fun insertAlert(alertWeather: AlertWeather){
        localWeather.insertAlert(alertWeather)
    }

    override fun deleteAlert(alertWeather: AlertWeather){
        localWeather.deleteAlert(alertWeather)
    }

    override fun getAlerts() : Flow<List<AlertWeather>>{
        return localWeather.getAlerts()
    }


}