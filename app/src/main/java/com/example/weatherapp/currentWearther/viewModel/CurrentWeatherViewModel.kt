package com.example.weatherapp.currentWearther.viewModel

import android.util.Log
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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


    fun getWeather(lon:Double , lat:Double , lang:String , unit:String , locationMethod:String){

        viewModelScope.launch(Dispatchers.IO){

            _weather.value = ApiStatus.Loading
            Log.i("TAG", "getWeather:  ${_weather.value}")
            repo.getDayForecast(lang).collect{
                 if(it != null) {
                    val dayWeather = it.list
                    val city = it.city

                    var status = false
                    val currentDate = getCurrentDateTime()


                    if (currentDate.equals(convertToDateFormat(dayWeather.get(0).dt_txt)) ) {
                        if (locationMethod.equals("GPS")) {
                            Log.i("TAG", "getWeather:  here ${repo.getLatitude()}  ${city.coord.lat}")
                            if (repo.getLatitude().equals(city.coord.lat) && repo.getLongitude()
                                    .equals(city.coord.lon)
                            ) {
                                _weather.value = ApiStatus.Success(it)
                            }
                        } else {

                            if (repo.getLatitude().equals(city.coord.lat) && repo.getLongitude()
                                    .equals(city.coord.lon)
                            ) {
                                _weather.value = ApiStatus.Success(it)
                            }else{
                            _weather.value = ApiStatus.Success(it)
                            }
                        }
                    } else {
                        repo.getWeather(lon, lat, lang, unit).collect {
                            it.city.coord.lat = repo.getLatitude()
                            it.city.coord.lon = repo.getLongitude()
                            it.lang = repo.getLanguage()
                            _weather.value = ApiStatus.Success(it)
                            _weather.value = ApiStatus.Success(it)
                            repo.insertDayForecast(it)
                        }

                    }
                }
                 else
                {
                    repo.getWeather(lon, lat, lang, unit).collect {
                        it.city.coord.lat = repo.getLatitude()
                        it.city.coord.lon = repo.getLongitude()
                        it.lang = repo.getLanguage()
                        _weather.value = ApiStatus.Success(it)
                        repo.insertDayForecast(it)
                    }

                }

            }
        }

        /*viewModelScope.launch(Dispatchers.IO) {
            repo.getWeather(lon, lat , lang , unit)/*.catch { _weather.value = ApiStatus.Failure(it) }*/.collect {
                _weather.value = ApiStatus.Success(it)
                repo.insertDayForecast(it)
            }
        }*/
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

    fun returnToLoading()
    {
        _weather.value = ApiStatus.Loading
    }

}