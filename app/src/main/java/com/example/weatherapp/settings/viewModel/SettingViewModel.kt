package com.example.weatherapp.settings.viewModel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.DayWeather
import com.example.weatherapp.model.IRepository
import com.example.weatherapp.network.ApiStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingViewModel(var repo: IRepository): ViewModel() {


    // language observation
    private var _language: MutableStateFlow<String> = MutableStateFlow<String>(repo.getLanguage())
    val language : StateFlow<String> = _language

    // unit observation
    private var _unit: MutableStateFlow<String> = MutableStateFlow<String>(repo.getUnit())
    val unit : StateFlow<String> = _unit

    // wind speed observation
    private var _wind: MutableStateFlow<String> = MutableStateFlow<String>(repo.getWindSpeed())
    val wind: StateFlow<String> = _wind

    // location method observation
    private var _method: MutableStateFlow<String> = MutableStateFlow<String>(repo.getLocationMethod())
    val method : StateFlow<String> = _method

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
        _unit.value = unit
    }

    fun setWindSpeed(wind:String)
    {
        repo.setWindSpeed(wind)
        _wind.value = wind
    }

    fun setLocationMethod(method:String)
    {
        repo.setLocationMethod(method)
        _method.value = method
    }

}