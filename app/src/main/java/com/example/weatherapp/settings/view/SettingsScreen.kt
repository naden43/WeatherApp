package com.example.weatherapp.settings.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import com.example.weatherapp.databinding.FragmentSettingsScreenBinding
import com.example.weatherapp.data.local.dayWeather.DayWeatherLocalDataSourceImpl
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.data.local.setting.SettingLocalDataSourceImpl
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.db.WeatherDataBase
import com.example.weatherapp.settings.viewModel.SettingViewModel
import com.example.weatherapp.settings.viewModel.SettingViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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

         val factory = SettingViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity()) , DayWeatherLocalDataSourceImpl.getInstance(
             WeatherDataBase.getInstance(requireContext()).getDayWeatherDao() ,
             WeatherDataBase.getInstance(requireContext()).getFavouriteDao() , WeatherDataBase.getInstance(requireContext()).getAlertWeatherDao() ) ))
        settingViewModel = ViewModelProvider(requireActivity() , factory).get(SettingViewModel::class.java)

        checkConnectivity()



        val lang = settingViewModel.getLanguage()

        /*lifecycleScope.launch {

            settingViewModel.language.collect {
                withContext(Dispatchers.Main) {
                    var local: Locale = Locale(it)
                    Locale.setDefault(local) // Set default locale
                    var resources: Resources = requireActivity().resources
                    var config: Configuration = resources.configuration
                    config.locale = local
                    resources.updateConfiguration(config, resources.displayMetrics)
                    if (it == "ar") {
                        requireActivity().window.decorView.layoutDirection =
                            View.LAYOUT_DIRECTION_RTL
                    } else {
                        requireActivity().window.decorView.layoutDirection =
                            View.LAYOUT_DIRECTION_LTR
                    }
                }
            }
        }*/

        if(lang == "en"){
            binding.englishSwitch.isChecked = true
        }
        else
        {
            binding.arabicSwitch.isChecked = true
        }

        val unit = settingViewModel.getUnit()
        if(unit == "metric")
        {
            binding.celesiusSwitch.isChecked = true
        }
        else if(unit == "imperial")
        {
            binding.fehrenhitSwitch.isChecked = true
        }
        else if(unit == "")
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
            binding.button.isEnabled = false
        }
        else
        {
            binding.cellularSwitch.isChecked = true
            binding.button.isEnabled = true

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
                binding.button.isEnabled = false
                settingViewModel.setSession(false)
                settingViewModel.deleteDataBase()
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
                binding.button.isEnabled = true
                settingViewModel.setSession(false)

                val action = SettingsScreenDirections.actionSettingsScreenToMapFragment2()
                action.id = 1
                findNavController(binding.root).navigate(action)
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
                settingViewModel.setUnit("metric")

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
                settingViewModel.setUnit("imperial")
            }

        }

        binding.kelvinSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                binding.celesiusSwitch.isChecked = false
                binding.fehrenhitSwitch.isChecked = false
                settingViewModel.setUnit("")
            }
        }

        binding.button.setOnClickListener {
            val action = SettingsScreenDirections.actionSettingsScreenToMapFragment2()
            action.id = 1
            findNavController(binding.root).navigate(action)
        }

    }
   override fun onStart() {
        super.onStart()

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {

                lifecycleScope.launch(Dispatchers.Main) {

                        binding.networkLayout.visibility = View.GONE
                        binding.settingLayout.visibility = View.VISIBLE

                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                lifecycleScope.launch(Dispatchers.Main){

                        Log.i("TAG", "onLost: nnnn ")
                        binding.networkLayout.visibility = View.VISIBLE
                        binding.settingLayout.visibility= View.GONE

                }

            }

        }


        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    private fun checkConnectivity(){
        val connectivityManager =
            requireActivity().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected)){
            binding.networkLayout.visibility = View.VISIBLE
            binding.settingLayout.visibility= View.GONE
        }

    }
}