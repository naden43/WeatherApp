package com.example.weatherapp.data.repository

import com.example.weatherapp.TestConstants
import com.example.weatherapp.alert.viewModel.Constants
import com.example.weatherapp.data.local.setting.FakeSettingDataSourceImpl
import com.example.weatherapp.data.local.setting.SettingLocalDataSource
import com.example.weatherapp.data.model.AlertWeather
import com.example.weatherapp.data.model.Data
import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.model.FavouriteWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : IRepository {


    var settings : LinkedHashMap<String, String> = LinkedHashMap()
    var localData :MutableList<FavouriteWeather> = mutableListOf()
    override fun getWeather(lat: Double, lon: Double): Flow<DayWeather> {
        TODO("Not yet implemented")
    }

    override fun getWeather(lat: Double, lon: Double, lang: String): Flow<DayWeather> {
        return flow { emit(DayWeather(lang = lang)) }
    }

    override fun getWeather(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<DayWeather> {
        return flow { emit(DayWeather(lang = lang)) }
    }

    override fun setLanguage(lang: String) {
        settings.put(TestConstants.LANGUAGE ,lang)
    }

    override fun setUnit(unit: String) {
        settings.put(TestConstants.UNIT , unit)
    }

    override fun setWindSpeed(wind: String) {
        settings.put(TestConstants.WIND , wind)
    }

    override fun setLocationMethod(location: String) {
        settings.put(TestConstants.LOCATION, location)
    }

    override fun getLanguage(): String {
        return settings.get(TestConstants.LANGUAGE)?:"en"
    }

    override fun getUnit(): String {
        return settings.get(TestConstants.UNIT)?:""
    }

    override fun getWindSpeed(): String {
        return settings.get(TestConstants.WIND)?:"meter/sec"
    }

    override fun getLocationMethod(): String {
        return settings.get(TestConstants.LOCATION)?:"GPS"
    }

    override fun getLongitude(): Double {
        TODO("Not yet implemented")
    }

    override fun getLatitude(): Double {
        TODO("Not yet implemented")
    }

    override fun setLatitude(lat: Double) {
        TODO("Not yet implemented")
    }

    override fun setLongitude(lon: Double) {
        TODO("Not yet implemented")
    }

    override fun insertDayForecast(dayWeather: DayWeather) {
        TODO("Not yet implemented")
    }

    override fun deleteDayForecast(dayWeather: DayWeather) {
        TODO("Not yet implemented")
    }

    override fun getDayForecast(lang: String): Flow<DayWeather> {
        TODO("Not yet implemented")
    }

    override fun getSession(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setSession(status: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getAllDayWeather(): Flow<List<DayWeather>> {
        TODO("Not yet implemented")
    }

    override fun deleteAllDayWeather() {
        TODO("Not yet implemented")
    }

    override fun insertFavourite(favouriteWeather: FavouriteWeather) {
        localData.add(favouriteWeather)
    }

    override fun deleteFavourite(favouriteWeather: FavouriteWeather) {
        localData.remove(favouriteWeather)
    }

    override fun getFavourite(lon: Double, lat: Double): Flow<FavouriteWeather> {
        TODO("Not yet implemented")
    }

    override fun getFavourites(): Flow<List<FavouriteWeather>> {
        return flow {
            emit(localData)
        }
    }

    override fun insertAlert(alertWeather: AlertWeather) {
        TODO("Not yet implemented")
    }

    override fun deleteAlert(alertWeather: AlertWeather) {
        TODO("Not yet implemented")
    }

    override fun getAlerts(): Flow<List<AlertWeather>> {
        TODO("Not yet implemented")
    }
}