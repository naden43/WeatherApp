package com.example.weatherapp

import android.app.Application
import android.util.Log
import com.example.weatherapp.data.local.setting.SettingLocalDataSource
import com.example.weatherapp.data.local.setting.SettingLocalDataSourceImpl

class MyApplication : Application() {

    /*lateinit var localDataSourceImpl: SettingLocalDataSource // Late initialization

    override fun onCreate() {
        super.onCreate()
        Log.i("TAG", "onCreate:  session false")
        localDataSourceImpl = SettingLocalDataSourceImpl.getInstance(this)
        localDataSourceImpl.setSession(false)
    }*/

}
