package com.example.weatherapp.settings.viewModel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.IRepository
import org.intellij.lang.annotations.Language

class SettingViewModel(var repo: IRepository): ViewModel() {


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
    }

    fun setUnit(unit:String)
    {
        repo.setUnit(unit)
    }

    fun setWindSpeed(wind:String)
    {
        repo.setWindSpeed(wind)
    }

    fun setLocationMethod(method:String)
    {
        repo.setLocationMethod(method)
    }

}