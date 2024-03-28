package com.example.weatherapp.data.local.setting

import com.example.weatherapp.TestConstants

class FakeSettingDataSourceImpl(private var settingMap : MutableMap<String , String> = mutableMapOf()): SettingLocalDataSource {
    override fun setLanguage(lang: String) {
        settingMap.put(TestConstants.LANGUAGE , lang)
    }

    override fun setUnit(unit: String) {
        TODO("Not yet implemented")
    }

    override fun setWindSpeed(wind: String) {
        settingMap.put(TestConstants.WIND , wind)
    }

    override fun setLocationMethod(locationMethod: String) {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): String {
        return settingMap.get(TestConstants.LANGUAGE)?:"en"
    }

    override fun getUnit(): String {
        TODO("Not yet implemented")
    }

    override fun getWindSpeed(): String {
        return settingMap.get(TestConstants.WIND)?:"meter/sec"
    }

    override fun getLocationMethod(): String {
        TODO("Not yet implemented")
    }

    override fun setLongitude(lon: Double) {
        TODO("Not yet implemented")
    }

    override fun setLatitude(lat: Double) {
        TODO("Not yet implemented")
    }

    override fun getLatitude(): Double {
        TODO("Not yet implemented")
    }

    override fun getLongitude(): Double {
        TODO("Not yet implemented")
    }

    override fun setSession(status: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getSession(): Boolean {
        TODO("Not yet implemented")
    }
}