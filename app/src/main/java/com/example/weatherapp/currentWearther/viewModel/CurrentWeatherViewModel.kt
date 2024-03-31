package com.example.weatherapp.currentWearther.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Data
import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.repository.IRepository
import com.example.weatherapp.network.ApiStatus

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CurrentWeatherViewModel(var repo: IRepository) : ViewModel(){


    private var _weather: MutableStateFlow<ApiStatus<DayWeather>> = MutableStateFlow<ApiStatus<DayWeather>>(ApiStatus.Loading)
    val weather : StateFlow<ApiStatus<DayWeather>> = _weather



    fun getWeather(lon:Double , lat:Double){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeather(lon, lat).catch { _weather.value = ApiStatus.Failure(it) }.collect {
                _weather.value = ApiStatus.Success(it)
            }
        }
    }

    fun getWeather(lon:Double , lat:Double , lang:String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeather(lon, lat , lang)/*.catch { _weather.value = ApiStatus.Failure(it) }*/.collect {
                _weather.value = ApiStatus.Success(it)
            }
        }
    }


    fun getWeather(lon:Double , lat:Double , lang:String , unit:String , locationMethod:String) {

        _weather.value = ApiStatus.Loading
        viewModelScope.launch(Dispatchers.IO) {
            if(repo.getSession() ){
                repo.getDayForecast(lang).collect{data ->

                    Log.i("TAG", "getWeather: get from db ")
                    if(data != null)
                    {
                        if(unit== "metric") {
                            _weather.value = ApiStatus.Success(convertTemperatureToCelsius(data))
                        }
                        else if(unit == "imperial")
                        {
                            _weather.value = ApiStatus.Success(convertTemperatureToFahrenheit(data))

                        }
                        else
                        {
                            _weather.value = ApiStatus.Success(data)
                        }

                    }
                    else{
                        Log.i("TAG", "getWeather: get from network  another language")
                        getDataFromApi(lon , lat , lang , unit )
                    }
                }
            }
            else
            {
                Log.i("TAG", "getWeather: get from network ")
                getDataFromApi(lon , lat , lang , unit )
            }
        }
    }

    fun convertKelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15 // Kelvin to Celsius conversion formula
    }

    fun convertTemperatureToCelsius(weatherResponse: DayWeather): DayWeather {
        for (item in weatherResponse.list) {
            item.main.temp = convertKelvinToCelsius(item.main.temp)
            item.main.feels_like = convertKelvinToCelsius(item.main.feels_like)
            item.main.temp_min = convertKelvinToCelsius(item.main.temp_min)
            item.main.temp_max = convertKelvinToCelsius(item.main.temp_max)
            item.main.temp_kf = convertKelvinToCelsius(item.main.temp_kf)
        }
        return weatherResponse
    }

    fun convertKelvinToFahrenheit(kelvin: Double): Double {
        return ((kelvin * 9) / 5) - 459.67 // Kelvin to Fahrenheit conversion formula
    }

    fun convertTemperatureToFahrenheit(weatherResponse: DayWeather): DayWeather {
        for (item in weatherResponse.list) {
            item.main.temp = convertKelvinToFahrenheit(item.main.temp)
            item.main.feels_like = convertKelvinToFahrenheit(item.main.feels_like)
            item.main.temp_min = convertKelvinToFahrenheit(item.main.temp_min)
            item.main.temp_max = convertKelvinToFahrenheit(item.main.temp_max)
            item.main.temp_kf = convertKelvinToFahrenheit(item.main.temp_kf)
        }
        return weatherResponse
    }
    fun getDataFromApi(lon:Double , lat:Double , lang:String  , unit: String)
    {
        _weather.value = ApiStatus.Loading
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeather(lon, lat, lang).catch {
                repo.getDayForecast(lang).collect { data ->

                    if(data !=null) {
                        Log.i("TAG", "getWeather: get from db ")
                        val currentDate = Calendar.getInstance().time

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                        val currentDateString = dateFormat.format(currentDate)

                        val inputApiFormat =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                        val date = inputApiFormat.parse(data.list.get(0).dt_txt)

                        val dateString = outputFormat.format(date!!)

                        if (currentDateString == dateString) {
                            if (unit == "metric") {
                                _weather.value =
                                    ApiStatus.Success(convertTemperatureToCelsius(data))
                            } else if (unit == "imperial") {
                                _weather.value =
                                    ApiStatus.Success(convertTemperatureToFahrenheit(data))

                            } else {
                                _weather.value = ApiStatus.Success(data)
                            }

                        } else {
                            _weather.value = ApiStatus.Failure(it)
                        }
                    }
                    else{
                        _weather.value = ApiStatus.Failure(it)
                    }
                }
            }.collect {
                if(!repo.getSession())
                {
                    repo.deleteAllDayWeather()
                }
                it.lang = repo.getLanguage()
                repo.insertDayForecast(it)
                repo.setSession(true)
                // _weather.value = ApiStatus.Success(it)
                if(unit== "metric") {
                    _weather.value = ApiStatus.Success(convertTemperatureToCelsius(it))
                }
                else if(unit == "imperial")
                {
                    _weather.value = ApiStatus.Success(convertTemperatureToFahrenheit(it))

                }
                else
                {
                    _weather.value = ApiStatus.Success(it)
                }
            }
        }
    }

    fun getCurrentTimeStamp() : Int
    {
        val currentDateTime = LocalDateTime.now()
        val currentHourTimeStamp = ((currentDateTime.hour / 24.0) * 8).toInt()

        return currentHourTimeStamp
    }

    fun reArrangeList(list:MutableList<Data>, currentHourTimeStamp:Int ) : MutableList<Data>{
        var i:Int = 0
        while (i <= currentHourTimeStamp && currentHourTimeStamp!=7 && currentHourTimeStamp!=0){
            val temp = list[i]
            list[i] = list[i+1]
            list[i+1] = temp
            i++
        }

        return list
    }

    fun getLocationText(location:String?): String
    {
        val parts = location?.split(',')
        val firstPart = parts?.first()?.trim()
        val lastPart = parts?.last()?.trim()
        return "$firstPart , $lastPart"
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())
        return dateFormat.format(date)
    }
    fun convertToDateFormat(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }
    fun getDateString(date:String) : String{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime =
            LocalDateTime.parse(
                date,
                formatter
            )
        val outputDateString =
            dateTime.format(
                DateTimeFormatter.ofPattern(
                    "EEE, dd MMMM",
                    Locale.getDefault()
                )
            )
        return outputDateString
    }

    fun getUnitSymbol(unit:String , context: Context) : String
    {
        if(unit == "metric")
        {
            return context.getString(R.string.celcius_symbol)
        }
        else if(unit == "imperial")
        {
            return context.getString(R.string.fahrenheit_symbol)
        }
        else
        {
            return context.getString(R.string.kelvin_symbol)
        }
    }

    fun returnToLoading()
    {
        _weather.value = ApiStatus.Loading
    }

}