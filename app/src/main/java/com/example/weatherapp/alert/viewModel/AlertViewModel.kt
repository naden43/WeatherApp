package com.example.weatherapp.alert.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherapp.data.model.AlertWeather
import com.example.weatherapp.data.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

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

        val requestId = UUID.fromString(alertWeather.requestId)
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteAlert(alertWeather)
            getAlerts()
        }
        WorkManager.getInstance().cancelWorkById(requestId)
    }

    fun addAlert(lat:Double , long:Double  , date:String , time:String , action:Int , context:Context)
    {

        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        val dateTimeString = "$date $time"
        val dateTime: Date = dateTimeFormat.parse(dateTimeString) ?: Date()
        Log.i("TAG", "addAlert: $dateTime ")

        val request = OneTimeWorkRequestBuilder<AlertWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                workDataOf(
                    Pair(Constants.LONGITUDE , long),
                    Pair(Constants.LATITUDE , lat),
                    Pair(Constants.LANGUAGE , repo.getLanguage()),
                    Pair(Constants.ACTION , action)
                )
            )
            .setInitialDelay(dateTime.time - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .build()

        Log.i("TAG", "addAlert: ${dateTime.time - System.currentTimeMillis()}")
        val requsetId = request.id
        viewModelScope.launch(Dispatchers.IO){
            val obj = AlertWeather(date , time , requsetId.toString())
            repo.insertAlert(obj)
            getAlerts()
        }

        WorkManager.getInstance(context).enqueue(request)

    }



}