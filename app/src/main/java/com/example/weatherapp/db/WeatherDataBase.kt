package com.example.weatherapp.db

import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import com.example.weatherapp.model.DayWeather


@Database(entities = [DayWeather::class], version = 1)
@TypeConverters(CityConverter::class, DataListConverter::class)
abstract class WeatherDataBase : RoomDatabase(){
    abstract fun getDayWeatherDao():DayWeatherDao
        companion object {
            @Volatile
            private var instance: WeatherDataBase? = null

            fun getInstance(context: Context): WeatherDataBase {
                return instance ?: synchronized(this) {
                    instance ?: buildDatabase(context).also { instance = it }
                }
            }
            private fun buildDatabase(context: Context): WeatherDataBase {
                return Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDataBase::class.java,
                    "WeatherDB"
                ).build()
            }
        }
}
