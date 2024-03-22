package com.example.weatherapp.model

interface SettingLocalDataSource {

    fun setLanguage(lang: String)

    fun setUnit(unit: String)

    fun setWindSpeed(wind: String)

    fun setLocationMethod(locationMethod: String)

    fun getLanguage(): String

    fun getUnit(): String

    fun getWindSpeed(): String

    fun getLocationMethod(): String
    fun setLongitude(lon: Double)

    fun setLatitude(lat: Double)

    fun getLatitude() : Double
    fun getLongitude() : Double

    fun setSession(status: Boolean)
    fun getSession(): Boolean
}