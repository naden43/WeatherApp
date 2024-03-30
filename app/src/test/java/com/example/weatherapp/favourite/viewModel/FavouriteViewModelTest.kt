package com.example.weatherapp.favourite.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.local.dayWeather.FakeDayWeatherLocalDataSourceImpl
import com.example.weatherapp.data.local.setting.FakeSettingDataSourceImpl
import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.model.FavouriteWeather
import com.example.weatherapp.data.remote.FakeWeatherRemoteDataSourceImpl
import com.example.weatherapp.data.repository.FakeRepository
import com.example.weatherapp.data.repository.IRepository
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.network.ApiStatus
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsInstanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FavouriteViewModelTest
{

    @get:Rule
    val instance = InstantTaskExecutorRule()
    lateinit var favoriteViewModel:FavouriteViewModel

    @Before
    fun setUp()
    {
        val repo = FakeRepository()
        favoriteViewModel  = FavouriteViewModel(repo)
    }

    @Test
    fun getDataFromRoom_insertThreeFavourite_retrieveDataFromRoom() = runBlockingTest{

        // given

        val fav1 = FavouriteWeather("egypt" , 1.6 , 9.7)
        val fav2 = FavouriteWeather("oman" , 9.7, 6.9)
        val fav3 = FavouriteWeather("alexandria" , 7.8 , 8.9)

        val tempResult = mutableListOf(fav1 , fav2 , fav3)

        favoriteViewModel.addFavourite(fav1)
        favoriteViewModel.addFavourite(fav2)
        favoriteViewModel.addFavourite(fav3)


        //when
        var result = mutableListOf<FavouriteWeather>()
        favoriteViewModel.getDataFromRoom()
        favoriteViewModel.favPlaces.take(1).collect{
            result = it
        }

        result.sortBy { it.countryName }
        tempResult.sortBy { it.countryName }

        //then
        assertThat(result , `is`(tempResult))

    }

    @Test
    fun getDataFromRoom_noData_emptyList() = runBlockingTest{


        //when
        var result = mutableListOf<FavouriteWeather>()
        favoriteViewModel.getDataFromRoom()
        favoriteViewModel.favPlaces.take(1).collect{
            result = it
        }

        //then
        assertThat(result , `is`(listOf()))

    }

    @Test
    fun getFavouriteWeather_LongitudeAndLatitude_DayWeather() = runTest{

        //when
        var result:ApiStatus<DayWeather>? = null


        favoriteViewModel.getFavouriteWeather(1.4 , 1.2)
        favoriteViewModel.weather.take(1).collect{
            result = it
        }

        //then
        assertThat(result , IsInstanceOf(ApiStatus.Success::class.java))

    }

    @Test
    fun addFavourite_oneFavourite_listOfSizeONE()= runBlockingTest{

        //given
        val fav1 = FavouriteWeather("egypt" , 1.6 , 9.7)

        //when
        favoriteViewModel.addFavourite(fav1)

        var result = mutableListOf<FavouriteWeather>()
        favoriteViewModel.getDataFromRoom()
        favoriteViewModel.favPlaces.take(1).collect{
            result = it
        }


        //then
        assertThat(result.size , `is`(1))

    }

    @Test
    fun deleteFavourite_insertTwoFavouriteAndDeleteOne_listOfSizeONE() = runBlockingTest {

        //given
        val fav1 = FavouriteWeather("egypt" , 1.6 , 9.7)
        val fav2 = FavouriteWeather("oman" , 9.7, 6.9)


        favoriteViewModel.addFavourite(fav1)
        favoriteViewModel.addFavourite(fav2)


        //when
        favoriteViewModel.deleteFavourite(fav1)

        var result = mutableListOf<FavouriteWeather>()
        favoriteViewModel.favPlaces.take(1).collect{
            result = it
        }


        //then
        assertThat(result.size , `is`(1))

    }



}