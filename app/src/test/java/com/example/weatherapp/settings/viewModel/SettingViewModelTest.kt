package com.example.weatherapp.settings.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.repository.FakeRepository
import com.example.weatherapp.favourite.viewModel.FavouriteViewModel
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SettingViewModelTest{

    @get:Rule
    val instance = InstantTaskExecutorRule()

    lateinit var settingViewModel: SettingViewModel

    @Before
    fun setUp()
    {
        val repo = FakeRepository()
        settingViewModel  = SettingViewModel(repo)
    }

    @Test
    fun getLanguage_defaultEN()
    {

        //when
        val result = settingViewModel.getLanguage()

        //then
        assertThat(result  , `is`("en"))

    }

    @Test
    fun setLanguage_langAR_getArabicLangUpdatedInlanguage()= runBlockingTest{

        //when
        settingViewModel.setLanguage("ar")
        val result = settingViewModel.getLanguage()
        var lang :String? = null
        settingViewModel.language.take(1).collect{
            lang = it
        }

        //then
        assertThat(result  , `is`("ar"))
        assertThat(lang , `is`(result))

    }

    @Test
    fun getUnit_defaultEmpty()
    {
        // when
        settingViewModel.getUnit()

        val unit = settingViewModel.getUnit()

        //then
        assertThat(unit , `is`(""))
    }

    @Test
    fun setUnit_unitMetric_metric()
    {
        //when
        settingViewModel.setUnit("metric")

        val unit = settingViewModel.getUnit()

        //then
        assertThat(unit, `is`("metric"))
    }

    @Test
    fun getLocationMethod_retrieveDefaultGPS()
    {
        //when
        val result = settingViewModel.getLocationMethod()

        //then
        assertThat(result, `is`("GPS"))
    }

    @Test
    fun setLocationMethod_MAP_retrieveMAP()
    {
        //when
        settingViewModel.setLocationMethod("MAP")

        val result = settingViewModel.getLocationMethod()


        //then
        assertThat(result , `is`("MAP"))
    }

}