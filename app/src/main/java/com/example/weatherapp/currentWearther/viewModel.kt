package com.example.weatherapp.currentWearther

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.DayWeather
import com.example.weatherapp.model.Repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class viewModel : ViewModel(){


    private var _weather: MutableLiveData<DayWeather> = MutableLiveData<DayWeather>()
    val weather : LiveData<DayWeather> = _weather

    var repo: Repository = Repository.Instance()



    fun getWeather(lon:Double , lat:Double){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeather(lon, lat).collect {
                _weather.postValue(it)
            }
        }
    }


}