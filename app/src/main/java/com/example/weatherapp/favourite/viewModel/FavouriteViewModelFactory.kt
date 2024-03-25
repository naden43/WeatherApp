package com.example.weatherapp.favourite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.currentWearther.viewModel.CurrentWeatherViewModel
import com.example.weatherapp.model.IRepository


@Suppress("UNCHECKED_CAST")
class FavouriteViewModelFactory(var repository: IRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavouriteViewModel(repository) as T
        }
}
