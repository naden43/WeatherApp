package com.example.weatherapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.local.dayWeather.FakeDayWeatherLocalDataSourceImpl
import com.example.weatherapp.data.local.setting.FakeSettingDataSourceImpl
import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.remote.FakeWeatherRemoteDataSourceImpl
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RepositoryTest{

    @get:Rule
    val instance = InstantTaskExecutorRule()


    lateinit var repo :IRepository

    lateinit var weatherList:MutableList<DayWeather>

    @Before
    fun setUp()
    {
        weatherList = mutableListOf(
            DayWeather(lang = "ar")
        )
        val remoteWeather = FakeWeatherRemoteDataSourceImpl()
        val localWeather  = FakeDayWeatherLocalDataSourceImpl(weatherList)
        val localSetting = FakeSettingDataSourceImpl()
        repo  = Repository(remoteWeather , localSetting , localWeather)
    }

    @Test
    fun insertDayForecast_DayWeatherWithEnLang_DayWeatherOfEnLang() = runBlockingTest {

         //Given
         val dayWeather = DayWeather(lang = "en")

        // when
        repo.insertDayForecast(dayWeather)

        var result = DayWeather()
        repo.getDayForecast("en").collect{
            result = it
        }

        // then
        assertThat(result , `is`(dayWeather))
    }

    @Test
    fun deleteAllDayWeather_noInput_EmptyList() = runBlockingTest {

        // when
        repo.deleteAllDayWeather()

        var result = mutableListOf<DayWeather>()
        repo.getAllDayWeather().collect{
            result.addAll(it)
        }

        // then
        assertThat(result , `is`(mutableListOf<DayWeather>()))

    }

    @Test
    fun getAllDayWeather_listOfOneWeather_listOfOneWeather() = runBlockingTest {
        // when
        var result = mutableListOf<DayWeather>()
        repo.getAllDayWeather().collect{
            result.addAll(it)
        }

        // then

        assertThat(result , `is`(weatherList))
    }

    @Test
    fun setLanguage_en_en()
    {
        // when
        repo.setLanguage("ar")

        val result = repo.getLanguage()

        // then
        assertThat(result , `is`("ar"))
    }

    @Test
    fun setWindSpeed_meterPerSec_meterPerSec() {
        // when
        repo.setWindSpeed("meter/sec")

        val result = repo.getWindSpeed()

        //then

        assertThat(result , `is`("meter/sec"))
    }


    @Test
    fun getWindSpeed_noInput_defaultValueMeterPerSec()
    {
        //when
        val result = repo.getWindSpeed()

        //then

        assertThat(result , `is`("meter/sec"))
    }


    @Test
    fun getLanguage_noInput_defaultValueEn()
    {
        // when
        val result = repo.getLanguage()

        // then
        assertThat(result , `is`("en"))
    }
}