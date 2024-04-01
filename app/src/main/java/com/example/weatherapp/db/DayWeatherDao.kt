package com.example.weatherapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.model.DayWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface DayWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDayForecast(dayWeather: DayWeather):Long
    @Delete
    fun deleteDayForecast(dayWeather: DayWeather)
    @Query("SELECT * FROM day_weather WHERE lang = :language")
    fun getDayWeather(language: String): Flow<DayWeather>

    @Query("SELECT * FROM day_weather")
    fun getAllDayWeather(): Flow<List<DayWeather>>

    @Query("DELETE FROM day_weather")
    fun deleteAllWeathers()

}