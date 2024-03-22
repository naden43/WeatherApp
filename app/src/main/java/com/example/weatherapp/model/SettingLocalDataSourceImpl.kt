package com.example.weatherapp.model

import android.content.Context
import android.content.SharedPreferences

class SettingLocalDataSourceImpl private constructor(var context: Context) : SettingLocalDataSource {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private val FILENAME = "setting file"
    private val LANGUAGE = "language"
    private val UNIT = "unit"
    private val WIND_SPEED = "wind speed"
    private val LOCATION_METHOD = "location method"
    private val LONGITIUDE = "longitude"
    private val LATITUDE = "latitude"
    private val SESSION = "session"





    companion object{
        var INSTANCE : SettingLocalDataSourceImpl? = null
        fun getInstance(context: Context): SettingLocalDataSourceImpl{
            return INSTANCE?: synchronized(this){
                val instance = SettingLocalDataSourceImpl(context)
                INSTANCE = instance
                instance
            }
        }
    }

    init {
        sharedPreferences = context.getSharedPreferences(FILENAME , Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        if(!sharedPreferences.contains(LANGUAGE))
        {
            setLanguage("en")
            setUnit("metric")
            setWindSpeed("meter/sec")
            setLocationMethod("GPS")
            setSession(false)
        }
    }

    override fun setLanguage(lang:String) {
        editor.putString(LANGUAGE , lang)
        editor.commit()
    }

    override fun setUnit(unit:String) {
        editor.putString(UNIT , unit)
        editor.commit()
    }

    override fun setWindSpeed(wind:String) {
        editor.putString(WIND_SPEED , wind)
        editor.commit()
    }

    override fun setLocationMethod(locationMethod:String) {
        editor.putString(LOCATION_METHOD ,locationMethod)
        editor.commit()
    }

    override fun getLanguage(): String {
        return sharedPreferences.getString(LANGUAGE , "en")!!
    }

    override fun getUnit(): String {
        return sharedPreferences.getString(UNIT , "celsius")!!
    }

    override fun getWindSpeed(): String {
        return sharedPreferences.getString(WIND_SPEED , "meter/sec")!!
    }

    override fun getLocationMethod(): String {

        return sharedPreferences.getString(LOCATION_METHOD, "GPS")!!
    }

    override fun setLongitude(lon:Double)
    {
        editor.putString(LONGITIUDE , lon.toString())
        editor.commit()
    }

    override fun setLatitude(lat:Double){

        editor.putString(LATITUDE , lat.toString())
        editor.commit()
    }

    override fun setSession(status:Boolean){
        editor.putBoolean(SESSION , status)
        editor.commit()
    }

    override fun getSession() : Boolean{
        return sharedPreferences.getBoolean(SESSION , false)
    }

    override fun getLongitude() : Double
    {
        return sharedPreferences.getString(LONGITIUDE ,"0.0" )!!.toDouble()
    }

    override fun getLatitude() : Double
    {
        return sharedPreferences.getString(LATITUDE ,"0.0" )!!.toDouble()
    }
}