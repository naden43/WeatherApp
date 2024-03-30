package com.example.weatherapp.favourite.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.data.model.FavouriteWeather
import com.example.weatherapp.data.repository.IRepository
import com.example.weatherapp.network.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouriteViewModel(var repo: IRepository) : ViewModel() {

    private var _favPlaces: MutableStateFlow<MutableList<FavouriteWeather>> = MutableStateFlow<MutableList<FavouriteWeather>>(
        mutableListOf())
    val favPlaces : StateFlow<MutableList<FavouriteWeather>> = _favPlaces

    private var _weather: MutableStateFlow<ApiStatus<DayWeather>> = MutableStateFlow<ApiStatus<DayWeather>>(ApiStatus.Loading)
    val weather : StateFlow<ApiStatus<DayWeather>> = _weather

    init {
        getDataFromRoom()
    }



    fun getDataFromRoom()
    {
        viewModelScope.launch(Dispatchers.IO){
            repo.getFavourites().collect{data ->
                if (data == null){
                    _favPlaces.value = mutableListOf()
                }
                else {
                    _favPlaces.value = data.toMutableList()
                }
            }
        }
    }
    fun getFavouriteWeather(lon:Double , lat:Double)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeather(lat , lon , repo.getLanguage() , repo.getUnit()).collect {
                _weather.value = ApiStatus.Success(it)
            }
        }
    }

    fun addFavourite(favouriteWeather: FavouriteWeather)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFavourite(favouriteWeather)
            getDataFromRoom()
        }
    }

    fun deleteFavourite(favouriteWeather: FavouriteWeather)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFavourite(favouriteWeather)
            getDataFromRoom()
        }
    }

}