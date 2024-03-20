package com.example.weatherapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.DayWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface DayWeatherDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDayForecast(dayWeather: DayWeather):Long
    @Delete
    fun deleteDayForecast(dayWeather: DayWeather)
    @Query("SELECT * FROM day_weather WHERE lang = :language")
    fun getDayWeather(language: String): Flow<DayWeather>

}