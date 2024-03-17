package com.example.weatherapp.settings.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.FragmentSettingsScreenBinding
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.SettingLocalDataSourceImpl
import com.example.weatherapp.network.WeatherRemoteDataSourceImpl
import com.example.weatherapp.settings.viewModel.SettingViewModel
import com.example.weatherapp.settings.viewModel.SettingViewModelFactory

class SettingsScreen : Fragment() {


    lateinit var binding:FragmentSettingsScreenBinding
    lateinit var settingViewModel: SettingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingsScreenBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         val factory = SettingViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity())))
         settingViewModel = ViewModelProvider(requireActivity() , factory).get(SettingViewModel::class.java)

        val lang = settingViewModel.getLanguage()
        if(lang == "en"){
            binding.englishSwitch.isChecked = true
        }
        else
        {
            binding.arabicSwitch.isChecked = true
        }

        val unit = settingViewModel.getUnit()
        Log.i("TAG", "onViewCreated:  $unit")
        if(unit == "celsius")
        {
            binding.celesiusSwitch.isChecked = true
        }
        else if(unit == "Fahrenheit")
        {
            binding.fehrenhitSwitch.isChecked = true
        }
        else if(unit == "kelvin")
        {
            binding.kelvinSwitch.isChecked = true
        }

        val windSpeed = settingViewModel.getWindSpeed()
        if(windSpeed == "meter/sec")
        {
            binding.meterSwitch.isChecked = true
        }
        else
        {
            binding.miliesSwitch.isChecked = true
        }

        val loactionMethod = settingViewModel.getLocationMethod()
        if(loactionMethod == "GPS")
        {
            binding.gpsSwitch.isChecked = true
        }
        else
        {
            binding.cellularSwitch.isChecked = true
        }

        binding.englishSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.arabicSwitch.isChecked = false
                settingViewModel.setLanguage("en")
            }
            else
            {
                binding.arabicSwitch.isChecked = true
            }
        }
        binding.arabicSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.englishSwitch.isChecked = false
                settingViewModel.setLanguage("ar")
            }
            else
            {
                binding.englishSwitch.isChecked = true
            }
        }

        binding.meterSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                binding.miliesSwitch.isChecked = false
                settingViewModel.setWindSpeed("meter/sec")
            }
            else
            {
                binding.miliesSwitch.isChecked = true
            }
        }

        binding.miliesSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                binding.meterSwitch.isChecked = false
                settingViewModel.setWindSpeed("miles/hour")
            }
            else
            {
                binding.meterSwitch.isChecked = true
            }
        }

        binding.gpsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                binding.cellularSwitch.isChecked = false
                settingViewModel.setLocationMethod("GPS")
            }
            else
            {
                binding.cellularSwitch.isChecked = true
            }
        }

        binding.cellularSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                binding.gpsSwitch.isChecked = false
                settingViewModel.setLocationMethod("MAP")
            }
            else
            {
                binding.gpsSwitch.isChecked = true
            }
        }

        binding.celesiusSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                binding.fehrenhitSwitch.isChecked = false
                binding.kelvinSwitch.isChecked = false

            }
            else
            {
                binding.fehrenhitSwitch.isChecked = true
            }
        }
        binding.fehrenhitSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                binding.celesiusSwitch.isChecked = false
                binding.kelvinSwitch.isChecked = false
            }

        }

        binding.kelvinSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                binding.celesiusSwitch.isChecked = false
                binding.fehrenhitSwitch.isChecked = false
            }

        }

    }
}