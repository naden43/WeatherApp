package com.example.weatherapp.alert.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.AlertWeather
import com.example.weatherapp.model.FavouriteWeather
import com.example.weatherapp.model.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertViewModel(var repo : IRepository) : ViewModel() {


    private var _alerts: MutableStateFlow<MutableList<AlertWeather>> = MutableStateFlow<MutableList<AlertWeather>>(
        mutableListOf())
    val alerts : StateFlow<MutableList<AlertWeather>> = _alerts
    init {
        getAlerts()
    }

    fun getAlerts()
    {
        viewModelScope.launch(Dispatchers.IO){
            repo.getAlerts().collect{
                _alerts.value = it.toMutableList()
            }
        }
    }

    fun deleteAlert(alertWeather: AlertWeather){



    }

    fun addAlert(lat:Double , long:Double)
    {

    }



}