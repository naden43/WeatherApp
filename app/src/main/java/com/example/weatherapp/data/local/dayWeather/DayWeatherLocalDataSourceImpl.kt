package com.example.weatherapp.data.local.dayWeather

import android.content.Context
import com.example.weatherapp.db.AlertWeatherDao
import com.example.weatherapp.db.DayWeatherDao
import com.example.weatherapp.db.FavouriteWeatherDao
import com.example.weatherapp.db.WeatherDataBase
import com.example.weatherapp.data.model.AlertWeather
import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.model.FavouriteWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DayWeatherLocalDataSourceImpl(
     var dayWeatherDao: DayWeatherDao,
     var favouriteWeatherDao: FavouriteWeatherDao,
     var alertWeatherDao: AlertWeatherDao
) : DayWeatherLocalDataSource {



    companion object{
        @Volatile
        private var INSTANCE : DayWeatherLocalDataSourceImpl? = null

        fun getInstance(  dayWeatherDao: DayWeatherDao,
                          favouriteWeatherDao: FavouriteWeatherDao,
                          alertWeatherDao: AlertWeatherDao) : DayWeatherLocalDataSourceImpl
        {
            return INSTANCE ?:synchronized(this){
                val instance = DayWeatherLocalDataSourceImpl(dayWeatherDao , favouriteWeatherDao , alertWeatherDao)
                INSTANCE = instance
                instance
            }
        }

    }


    override fun insertDayForecast(dayWeather: DayWeather) {
           dayWeatherDao.insertDayForecast(dayWeather)
    }

    override fun deleteDayForecast(dayWeather: DayWeather) {
        dayWeatherDao.deleteDayForecast(dayWeather)
    }

    override fun getDayForecast(lang:String): Flow<DayWeather> {
        return  dayWeatherDao.getDayWeather(lang)
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
        alertWeatherDao.deleteAlert(alertWeather)
    }



}