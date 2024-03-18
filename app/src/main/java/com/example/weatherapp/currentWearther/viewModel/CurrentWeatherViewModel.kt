package com.example.weatherapp.currentWearther.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Data
import com.example.weatherapp.model.DayWeather
import com.example.weatherapp.model.IRepository
import com.example.weatherapp.network.ApiStatus

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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


    fun getWeather(lon:Double , lat:Double , lang:String , unit:String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeather(lon, lat , lang , unit)/*.catch { _weather.value = ApiStatus.Failure(it) }*/.collect {
                _weather.value = ApiStatus.Success(it)
            }
        }
    }

    fun getCurrentTimeStamp() : Int
    {
        val currentDateTime = LocalDateTime.now()
        val currentHourTimeStamp = ((currentDateTime.hour / 24.0) * 8).toInt()

        return currentHourTimeStamp
    }

    fun reArrangeList(list:MutableList<Data> , currentHourTimeStamp:Int ) : MutableList<Data>{
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
                    Locale.ENGLISH
                )
            )
        return outputDateString
    }

    fun getUnitSymbol(unit:String) : String
    {
        if(unit == "metric")
        {
            return "C"
        }
        else if(unit == "imperial")
        {
            return "F"
        }
        else
        {
            return "K"
        }
    }

}