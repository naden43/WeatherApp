package com.example.weatherapp.model

import android.content.Context
import com.example.weatherapp.db.AlertWeatherDao
import com.example.weatherapp.db.DayWeatherDao
import com.example.weatherapp.db.FavouriteWeatherDao
import com.example.weatherapp.db.WeatherDataBase
import kotlinx.coroutines.flow.Flow

class DayWeatherLocalDataSourceImpl(context: Context) : DayWeatherLocalDataSource {


    lateinit var dayWeatherDao: DayWeatherDao
    lateinit var dataBase: WeatherDataBase
    lateinit var favouriteWeatherDao: FavouriteWeatherDao
    lateinit var alertWeatherDao: AlertWeatherDao
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
        favouriteWeatherDao = dataBase.getFavouriteDao()
        alertWeatherDao = dataBase.getAlertWeatherDao()
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
    override fun getAllDayForecast(): Flow<List<DayWeather>> {
        return dayWeatherDao.getAllDayWeather()
    }

    override fun deleteAllDays(){
        return dayWeatherDao.deleteAllWeathers()
    }

    override fun insertFavourite(favouriteWeather: FavouriteWeather){
        favouriteWeatherDao.insertFavourite(favouriteWeather)
    }

    override fun deleteFavourite(favouriteWeather: FavouriteWeather){
        favouriteWeatherDao.deleteFavourite(favouriteWeather)
    }

    override fun getFavourite(lon:Double , lat:Double) : Flow<FavouriteWeather>{
       return favouriteWeatherDao.getFavourite(lon , lat)
    }


    override fun getFavourites() : Flow<List<FavouriteWeather>>{
        return favouriteWeatherDao.getFavourites()
    }

    override fun getAlerts() : Flow<List<AlertWeather>>{
        return alertWeatherDao.getAlerts()
    }
    override fun insertAlert(alertWeather: AlertWeather){
        alertWeatherDao.insertAlert(alertWeather)
    }

    override fun deleteAlert(alertWeather: AlertWeather){
        alertWeatherDao.insertAlert(alertWeather)
    }



}