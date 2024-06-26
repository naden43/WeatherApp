package com.example.weatherapp.settings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingViewModel(var repo: IRepository): ViewModel() {


    // language observation
    private var _language: MutableStateFlow<String> = MutableStateFlow<String>(repo.getLanguage())
    val language : StateFlow<String> = _language

    fun getLanguage() :String
    {
        return repo.getLanguage()
    }

    fun getUnit():String
    {
        return repo.getUnit()
    }

    fun getWindSpeed():String
    {
        return repo.getWindSpeed()
    }

    fun getLocationMethod():String{
        return repo.getLocationMethod()
    }

    fun setLanguage(language: String)
    {
        repo.setLanguage(language)
        _language.value = language
    }

    fun setUnit(unit:String)
    {
        repo.setUnit(unit)
        //_unit.value = unit
    }

    fun deleteDataBase()
    {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAllDayWeather()
        }
    }



    fun setWindSpeed(wind:String)
    {
        repo.setWindSpeed(wind)
        //_wind.value = wind
    }

    fun setLocationMethod(method:String)
    {
        repo.setLocationMethod(method)
        //_method.value = method
    }

    fun setLongitude(lon:Double){
        repo.setLongitude(lon)
    }

    fun setLatitude(lat:Double)
    {
        repo.setLatitude(lat)
    }

    fun getLongitude() : Double
    {
        return repo.getLongitude()
    }

    fun getLatitude(): Double
    {
        return repo.getLatitude()
    }

    fun setSession(status:Boolean)
    {
        repo.setSession(status)
    }

    fun getSession() : Boolean
    {
        return repo.getSession()
    }

}