package com.example.weatherapp.currentWearther.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.IRepository


@Suppress("UNCHECKED_CAST")
    class CurrectWeatherFactory(var repository: IRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CurrentWeatherViewModel(repository) as T
        }

    }
