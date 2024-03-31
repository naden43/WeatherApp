package com.example.weatherapp.favourite.view

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.currentWearther.view.DailyWeatherAdapter
import com.example.weatherapp.currentWearther.view.DayWeatherAdapter
import com.example.weatherapp.currentWearther.viewModel.CurrectWeatherFactory
import com.example.weatherapp.currentWearther.viewModel.CurrentWeatherViewModel
import com.example.weatherapp.databinding.FragmentFavDetailsBinding
import com.example.weatherapp.favourite.viewModel.FavouriteViewModel
import com.example.weatherapp.favourite.viewModel.FavouriteViewModelFactory
import com.example.weatherapp.data.model.Data
import com.example.weatherapp.data.local.dayWeather.DayWeatherLocalDataSourceImpl
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.data.local.setting.SettingLocalDataSourceImpl
import com.example.weatherapp.network.ApiStatus
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.db.WeatherDataBase
import com.example.weatherapp.settings.viewModel.SettingViewModel
import com.example.weatherapp.settings.viewModel.SettingViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class FavDetails : Fragment() {

    lateinit var binding:FragmentFavDetailsBinding
    lateinit var currentWeather: CurrentWeatherViewModel
    lateinit var settings:SettingViewModel
    lateinit var favouriteViewModel: FavouriteViewModel
    var latitude:Double? = null
    var longitude:Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavDetailsBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = CurrectWeatherFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity()) , DayWeatherLocalDataSourceImpl.getInstance(
            WeatherDataBase.getInstance(requireContext()).getDayWeatherDao() ,
            WeatherDataBase.getInstance(requireContext()).getFavouriteDao() , WeatherDataBase.getInstance(requireContext()).getAlertWeatherDao() ) ))
        currentWeather = ViewModelProvider(this , factory).get(CurrentWeatherViewModel::class.java)

        val settingFactory = SettingViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity()) , DayWeatherLocalDataSourceImpl.getInstance(WeatherDataBase.getInstance(requireContext()).getDayWeatherDao() ,WeatherDataBase.getInstance(requireContext()).getFavouriteDao() , WeatherDataBase.getInstance(requireContext()).getAlertWeatherDao() ) ))
        settings = ViewModelProvider(requireActivity() , settingFactory).get(SettingViewModel::class.java)

        val favFactory = FavouriteViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity()) , DayWeatherLocalDataSourceImpl.getInstance(WeatherDataBase.getInstance(requireContext()).getDayWeatherDao() ,WeatherDataBase.getInstance(requireContext()).getFavouriteDao() , WeatherDataBase.getInstance(requireContext()).getAlertWeatherDao() ) ))
        favouriteViewModel = ViewModelProvider(requireActivity() , favFactory).get(FavouriteViewModel::class.java)

        if (arguments != null)
        {
            latitude = arguments?.getString("latitude")?.toDouble() ?: 0.0
            longitude =  arguments?.getString("longitude")?.toDouble() ?: 0.0

            if(latitude!=0.0 && longitude!=0.0) {

                favouriteViewModel.getFavouriteWeather(longitude!! ,latitude!!)
            }

        }


        lifecycleScope.launch {
            favouriteViewModel.weather.collect {result ->

                when(result){

                    is ApiStatus.Success -> {


                        withContext(Dispatchers.Main) {

                            binding.loader.visibility = View.GONE
                            binding.details.visibility = View.VISIBLE

                            var returnedData = result.data

                            var currentHourTimeStamp = currentWeather.getCurrentTimeStamp()

                            //currentWeather.reArrangeList(returnedData.list.toMutableList() , currentHourTimeStamp)

                            returnedData.list.get(currentHourTimeStamp).currentTime = true

                            //download icon
                            var logo = returnedData.list.get(currentHourTimeStamp).weather.get(0).icon
                            Picasso.get().load("https://openweathermap.org/img/wn/${logo}@4x.png")
                                .into(binding.weatherImage)

                            // display my location
                            binding.locationTxt.text = if(returnedData.city.name != ""){ returnedData.city.name} else{"UnKnown"} //currentWeather.getLocationText(locationtext)


                            // display the date
                            binding.timeTxt.text = currentWeather.getDateString(returnedData.list.get(0).dt_txt)


                            binding.descriptionTxt.text =
                                returnedData.list.get(currentHourTimeStamp).weather.get(0).description

                            var symbol  = currentWeather.getUnitSymbol(settings.getUnit() , requireContext())

                            binding.tempTxt.text =
                                " ${returnedData.list.get(currentHourTimeStamp).main.temp.toInt().toString()}  $symbol"

                            val weatherAdapter = DayWeatherAdapter(requireActivity() , symbol)
                            weatherAdapter.submitList(returnedData.list.take(8))
                            binding.dayForeCastRecycularView.apply {
                                adapter = weatherAdapter
                            }

                            val dailyWeatherAdapter = DailyWeatherAdapter(requireActivity())
                            var count = 0
                            val listOfDays = mutableListOf<Data>()
                            while (count < 5) {
                                val timeStampInDay = 3 + (count * 8)
                                val dayElement = returnedData.list.get(timeStampInDay)
                                listOfDays.add(dayElement)
                                count++
                            }
                            dailyWeatherAdapter.submitList(listOfDays)
                            binding.dailyRecycularView.apply {
                                adapter = dailyWeatherAdapter
                            }

                            binding.cloudsTxt.text =
                                "${returnedData.list.get(currentHourTimeStamp).clouds.all} %"

                            binding.humidityTxt.text =
                                "${returnedData.list.get(currentHourTimeStamp).main.humidity} %"

                            binding.pressureTxt.text =
                                "${returnedData.list.get(currentHourTimeStamp).main.pressure} ${getString(
                                    R.string.pressure_unit)}"

                            if(settings.getWindSpeed() == "meter/sec" && settings.getUnit() == "imperial")
                            {
                                binding.windTxt.text =
                                    "${(returnedData.list.get(currentHourTimeStamp).wind.speed * 0.44704).toInt()}  ${getString(
                                        R.string.wind_meter_sec)}"
                            }
                            else if(settings.getWindSpeed() == "meter/sec")
                            {
                                binding.windTxt.text =
                                    "${(returnedData.list.get(currentHourTimeStamp).wind.speed).toInt()}  ${getString(
                                        R.string.wind_meter_sec)}"
                            }
                            else if(settings.getWindSpeed() == "miles/hour" && (settings.getUnit() == "metric" || settings.getUnit() == ""))
                            {
                                binding.windTxt.text =
                                    "${(returnedData.list.get(currentHourTimeStamp).wind.speed/ 0.44704).toInt()}  ${getString(
                                        R.string.wind_miles_hour)}"
                            }
                            else{
                                binding.windTxt.text =
                                    "${(returnedData.list.get(currentHourTimeStamp).wind.speed).toInt()}  ${getString(
                                        R.string.wind_miles_hour)}"
                            }


                        }

                    }
                    is ApiStatus.Failure -> {
                        // show view of notwerk conictivity
                    }
                    is ApiStatus.Loading -> {

                        binding.loader.visibility = View.VISIBLE
                        binding.details.visibility = View.GONE


                    }

                    else -> {}
                }

            }
        }

        binding.backBtn.setOnClickListener {
            requireFragmentManager().popBackStack()
        }

    }


}