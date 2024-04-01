package com.example.weatherapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.model.FavouriteWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavourite(favouriteWeather: FavouriteWeather):Long
    @Delete
    fun deleteFavourite(favouriteWeather: FavouriteWeather)
    @Query("SELECT * FROM fav_weather WHERE lon = :lon AND lat = :lat" )
    fun getFavourite(lon: Double , lat:Double): Flow<FavouriteWeather>

    @Query("SELECT * FROM fav_weather " )
    fun getFavourites(): Flow<List<FavouriteWeather>>

}