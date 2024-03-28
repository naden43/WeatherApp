package com.example.weatherapp.settings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.repository.IRepository


    @Suppress("UNCHECKED_CAST")
    class SettingViewModelFactory(var repository: IRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingViewModel(repository) as T
        }
    }
