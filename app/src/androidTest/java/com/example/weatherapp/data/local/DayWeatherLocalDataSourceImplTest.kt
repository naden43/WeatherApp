package com.example.weatherapp.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherapp.data.local.dayWeather.DayWeatherLocalDataSource
import com.example.weatherapp.data.local.dayWeather.DayWeatherLocalDataSourceImpl
import com.example.weatherapp.data.model.AlertWeather
import com.example.weatherapp.data.model.FavouriteWeather
import com.example.weatherapp.db.WeatherDataBase
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@MediumTest
class DayWeatherLocalDataSourceImplTest {


    lateinit var db :WeatherDataBase
    lateinit var localDataSource: DayWeatherLocalDataSource
    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            WeatherDataBase::class.java
        ).allowMainThreadQueries()
            .build()

        localDataSource = DayWeatherLocalDataSourceImpl(db.getDayWeatherDao() , db.getFavouriteDao() , db.getAlertWeatherDao())
    }

    @After
    fun tearDb() {
        db.close()
    }

    @Test
    fun insertAlert_alert_retrieveAlert() = runTest{

        // Given
        val alert = AlertWeather("2001-13-1" , "12:30" , "123456-98766")

        // when

        localDataSource.insertAlert(alert)
        var result :MutableList<AlertWeather> = mutableListOf()

        localDataSource.getAlerts().collect{
             result = it.toMutableList()
        }

        assertThat(result.get(0) , `is`(alert))
    }

    @Test
    fun deleteAlert_insertOneAlert_emptyList() = runTest{

        // Given
        val alert = AlertWeather("2001-13-1" , "12:30" , "123456-98766")

        // when
        localDataSource.insertAlert(alert)
        localDataSource.deleteAlert(alert)

        var result :MutableList<AlertWeather> = mutableListOf()

        localDataSource.getAlerts().collect{
            result = it.toMutableList()
        }

        assertThat(result , `is`(listOf()))
    }
    @Test
    fun insertFavourite_favourite_retrieveFavourites() = runTest{

        // Given
        val favourite = FavouriteWeather("egypt" , 0.0 , 0.0)

        // when
        localDataSource.insertFavourite(favourite)

        var result :MutableList<FavouriteWeather> = mutableListOf()
        localDataSource.getFavourites().collect{
            result = it.toMutableList()
        }

        assertThat(result.get(0) , `is`(favourite))
    }

    @Test
    fun deleteFavourite_insertOneFavourite_emptyList() = runTest{

        // Given
        val favourite = FavouriteWeather("egypt" , 0.0 , 0.0)

        // when
        localDataSource.insertFavourite(favourite)
        localDataSource.deleteFavourite(favourite)

        var result :MutableList<FavouriteWeather> = mutableListOf()
        localDataSource.getFavourites().collect{
            result = it.toMutableList()
        }
        assertThat(result , `is`(listOf()))
    }


    @Test
    fun insertFavourite_favourite_retrieveFavouriteByPrimaryKey() = runTest()
    {
        val favourite = FavouriteWeather("egypt" , 1.2 , 2.2)

        // when
        localDataSource.insertFavourite(favourite)

        var result :FavouriteWeather? = null
        localDataSource.getFavourite(1.2 , 2.2).collect{
            result = it
        }

        assertThat(result , `is`(favourite))
    }




}