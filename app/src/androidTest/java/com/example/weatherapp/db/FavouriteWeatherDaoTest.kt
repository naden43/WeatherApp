package com.example.weatherapp.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.data.model.City
import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.model.FavouriteWeather
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class FavouriteWeatherDaoTest {

    @get:Rule
    val instant = InstantTaskExecutorRule()


    lateinit var dao :FavouriteWeatherDao

    lateinit var database: WeatherDataBase
    @Before
    fun setUp()
    {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext()
            ,WeatherDataBase::class.java
        ).build()

        dao = database.getFavouriteDao()
    }

    @After
    fun tearDown()
    {
        database.close()
    }

    @Test
    fun insertFavourites_FavouriteForecastList_retrieveFavourites() = runBlockingTest{

        //Given
        val favourite1 = FavouriteWeather("egypt" , 1.2 , 3.6)
        val favourite2 = FavouriteWeather("egypt2" , 1.6 , 3.6)
        val favourite3 = FavouriteWeather("egypt3" , 1.2 , 8.6)

        val tempResult = listOf(favourite1 , favourite2 , favourite3)

        // when
        dao.insertFavourite(favourite1)
        dao.insertFavourite(favourite2)
        dao.insertFavourite(favourite3)

        dao.getFavourites()
        val result = dao.getFavourites()

        //then
        MatcherAssert.assertThat(result.first(), `is`(tempResult))
    }


    @Test
    fun deleteFavouritePlace_favourite_getByPrimaryKey() = runBlockingTest{

        //Given
        val favourite = FavouriteWeather("egypt" , 0.0 , 0.0)

        // when
        dao.insertFavourite(favourite)
        dao.deleteFavourite(favourite)
        val result = dao.getFavourite(0.0 , 0.0)

        //then
        MatcherAssert.assertThat(result.first(), `is`(nullValue()))
    }


    @Test
    fun deleteFavourite_insertOneFavourite_emptyList() = runBlockingTest{

        //Given
        val favourite = FavouriteWeather("egypt" , 0.0 , 0.0)

        // when
        dao.insertFavourite(favourite)
        dao.deleteFavourite(favourite)
        val result = dao.getFavourites()

        //then
        MatcherAssert.assertThat(result.first(), `is`(listOf()))
    }


}