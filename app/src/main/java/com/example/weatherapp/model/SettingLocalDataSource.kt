package com.example.weatherapp.model

interface SettingLocalDataSource {

    fun setLanguage(lang:String)

    fun setUnit(unit:String)

    fun setWindSpeed(wind:String)

    fun setLocationMethod(locationMethod:String)

    fun getLanguage():String

    fun getUnit(): String

    fun getWindSpeed() : String

    fun getLocationMethod() : String

}