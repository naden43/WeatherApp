package com.example.weatherapp.alert.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.R
import com.example.weatherapp.alert.viewModel.AlertViewModel
import com.example.weatherapp.alert.viewModel.AlertViewModelFactory
import com.example.weatherapp.databinding.FragmentAlertScreenBinding
import com.example.weatherapp.favourite.view.FavouriteAdapter
import com.example.weatherapp.favourite.view.FavouriteScreenDirections
import com.example.weatherapp.favourite.viewModel.FavouriteViewModel
import com.example.weatherapp.favourite.viewModel.FavouriteViewModelFactory
import com.example.weatherapp.model.AlertWeather
import com.example.weatherapp.model.DayWeatherLocalDataSourceImpl
import com.example.weatherapp.model.FavouriteWeather
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.SettingLocalDataSourceImpl
import com.example.weatherapp.network.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertScreen : Fragment() {


    lateinit var binding:FragmentAlertScreenBinding
    lateinit var alertViewModel: AlertViewModel
    lateinit var alertAdapter: AlertAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertScreenBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = AlertViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity()) , DayWeatherLocalDataSourceImpl.getInstance(requireContext()) ))
        alertViewModel = ViewModelProvider(requireActivity() , factory).get(AlertViewModel::class.java)

        if (arguments != null)
        {
            var latitude = arguments?.getString("latitude")?.toDouble() ?: 0.0
            var longitude =  arguments?.getString("longitude")?.toDouble() ?: 0.0

            if(latitude!=0.0 && longitude!=0.0) {
                // view the dialog
                alertViewModel.addAlert(latitude , longitude)
                arguments = null
            }
        }

        alertAdapter = AlertAdapter(requireContext())
        {
            alertViewModel.deleteAlert(it)
        }

        binding.alertList.adapter = alertAdapter

        lifecycleScope.launch(Dispatchers.IO){
            alertViewModel.alerts.collect {
                alertAdapter.submitList(it)
            }
        }

        binding.addAlert.setOnClickListener {
            val action = AlertScreenDirections.actionAlertScreenToMapFragment2()
            action.id = 3
            Navigation.findNavController(binding.root).navigate(action)
        }

    }


}