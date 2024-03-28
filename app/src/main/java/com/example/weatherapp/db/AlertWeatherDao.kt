package com.example.weatherapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.model.AlertWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlert(alertWeather: AlertWeather):Long
    @Delete
    fun deleteAlert(alertWeather: AlertWeather)
    /*@Query("SELECT * FROM alert_weather WHERE lon = :lon AND lat = :lat" )
    fun getFavourite(lon: Double , lat:Double): Flow<FavouriteWeather>*/

    @Query("SELECT * FROM alert_weather " )
    fun getAlerts(): List<AlertWeather>

}