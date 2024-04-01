package com.example.weatherapp.currentWearther.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.currentWearther.viewModel.CurrectWeatherFactory
import com.example.weatherapp.currentWearther.viewModel.CurrentWeatherViewModel
import com.example.weatherapp.databinding.FragmentHomeScreenBinding
import com.example.weatherapp.data.model.Data
import com.example.weatherapp.data.local.dayWeather.DayWeatherLocalDataSourceImpl
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.data.local.setting.SettingLocalDataSourceImpl
import com.example.weatherapp.network.ApiStatus
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.db.WeatherDataBase
import com.example.weatherapp.settings.viewModel.SettingViewModel
import com.example.weatherapp.settings.viewModel.SettingViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class HomeScreen : Fragment() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val REQUEST_CODE = 500
    lateinit var currentWeather: CurrentWeatherViewModel
    lateinit var settings:SettingViewModel

    lateinit var binding: FragmentHomeScreenBinding

    var locationtext : String? = null

    var longitude:Double = 0.0
    var latitude:Double = 0.0
    var langObserver  = null
    var unitObserver = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = CurrectWeatherFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity()) , DayWeatherLocalDataSourceImpl.getInstance(
            WeatherDataBase.getInstance(requireContext()).getDayWeatherDao() ,
            WeatherDataBase.getInstance(requireContext()).getFavouriteDao() , WeatherDataBase.getInstance(requireContext()).getAlertWeatherDao() ) ))
        currentWeather = ViewModelProvider(this , factory).get(CurrentWeatherViewModel::class.java)

        val settingFactory = SettingViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity()) , DayWeatherLocalDataSourceImpl.getInstance(WeatherDataBase.getInstance(requireContext()).getDayWeatherDao() ,WeatherDataBase.getInstance(requireContext()).getFavouriteDao() , WeatherDataBase.getInstance(requireContext()).getAlertWeatherDao() ) ))
        settings = ViewModelProvider(requireActivity() , settingFactory).get(SettingViewModel::class.java)



        /*if (checkPermission()) {
            if (isLocationEnabled()) {
                getLocation()
            } else {
                enableLocation()

            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE
            )
        }*/


        /*lifecycleScope.launch(Dispatchers.IO){

            settings.language.collect {
                currentWeather.getWeather(longitude , latitude , it  , settings.getUnit())
            }

        }

        lifecycleScope.launch(Dispatchers.IO){
            settings.unit.collect{
                currentWeather.getWeather(longitude , latitude , settings.getLanguage() , it)
            }
        }*/

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeScreenBinding.inflate(inflater ,container,  false )


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.openPermissionBtn.setOnClickListener {
            Log.i("TAG", "getWeatherByLocationType: 0 ")

            getWeatherByLocationType()
       }



        lifecycleScope.launch {
            currentWeather.weather.collect {result ->

                when(result){

                    is ApiStatus.Success -> {

                        withContext(Dispatchers.Main) {
                            binding.loader.visibility = View.GONE
                            binding.homeLayout.visibility = View.VISIBLE
                            binding.networkLayout.visibility = View.GONE
                            binding.permission.visibility = View.GONE

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
                                "${returnedData.list.get(currentHourTimeStamp).main.pressure} ${getString(R.string.pressure_unit)}"

                            if(settings.getWindSpeed() == "meter/sec" && settings.getUnit() == "imperial")
                            {
                                binding.windTxt.text =
                                    "${(returnedData.list.get(currentHourTimeStamp).wind.speed * 0.44704).toInt()}  ${getString(R.string.wind_meter_sec)}"
                            }
                            else if(settings.getWindSpeed() == "meter/sec")
                            {
                                binding.windTxt.text =
                                    "${(returnedData.list.get(currentHourTimeStamp).wind.speed).toInt()}  ${getString(R.string.wind_meter_sec)}"
                            }
                            else if(settings.getWindSpeed() == "miles/hour" && (settings.getUnit() == "metric" || settings.getUnit() == ""))
                            {
                                binding.windTxt.text =
                                    "${(returnedData.list.get(currentHourTimeStamp).wind.speed/ 0.44704).toInt()}  ${getString(R.string.wind_miles_hour)}"
                            }
                            else{
                                binding.windTxt.text =
                                    "${(returnedData.list.get(currentHourTimeStamp).wind.speed).toInt()}  ${getString(R.string.wind_miles_hour)}"
                            }


                        }
                    }
                    is ApiStatus.Failure -> {
                        withContext(Dispatchers.Main) {
                            binding.loader.visibility = View.GONE
                            binding.homeLayout.visibility = View.GONE
                            binding.networkLayout.visibility = View.VISIBLE
                            binding.permission.visibility = View.GONE
                        }

                    }
                    is ApiStatus.Loading -> {
                        withContext(Dispatchers.Main) {
                            Log.i("TAG", "onViewCreated: hereeeeeeeeeee ")
                            binding.loader.visibility = View.VISIBLE
                            binding.homeLayout.visibility = View.GONE
                            binding.networkLayout.visibility = View.GONE
                            binding.permission.visibility = View.GONE
                        }

                    }

                }

            }
        }



    }


    override fun onStart() {
        super.onStart()


        checkConnectivity()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

            val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {

                    lifecycleScope.launch{
                        withContext(Dispatchers.Main)
                        {
                            Log.i("TAG", "onAvailable: avaliable  ")
                            getWeatherByLocationType()
                            binding.networkLayout.visibility = View.GONE
                            //Snackbar.make(binding.root , "You are connected now "  , Snackbar.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    /*runBlocking{
                        withContext(Dispatchers.Main)
                        {
                            if (binding.root!=null) {
                                Snackbar.make(
                                    binding.root,
                                    "You are not connected..",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                    }*/
                }

            }

            val connectivityManager =
                requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.requestNetwork(networkRequest, networkCallback)


    }

    fun getWeatherByLocationType()
    {
        if(settings.getLocationMethod() == "MAP") {

            currentWeather.getWeather(settings.getLongitude(), settings.getLatitude() , settings.getLanguage() , settings.getUnit() , settings.getLocationMethod())

        }
        else{

                Log.i("TAG", "getWeatherByLocationType: 2 ")

                if (checkPermission()) {
                    Log.i("TAG", "getWeatherByLocationType: 3 ")

                    if (isLocationEnabled()) {
                        getLocation()
                    } else {
                        enableLocation()

                    }
                } else {
                    Log.i("TAG", "getWeatherByLocationType: 4 ")

                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        REQUEST_CODE
                    )
                }
            }


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==REQUEST_CODE){
            if(grantResults.size >1 && (grantResults.get(0) == PackageManager.PERMISSION_GRANTED || grantResults.get(1)== PackageManager.PERMISSION_GRANTED)){
                getLocation()
            }
            else
            {
                binding.loader.visibility = View.GONE
                binding.homeLayout.visibility = View.GONE
                binding.networkLayout.visibility = View.GONE
                binding.permission.visibility = View.VISIBLE

            }
        }

    }

    fun enableLocation(){
        Toast.makeText(requireActivity() , "open location" , Toast.LENGTH_LONG).show()
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    fun checkPermission():Boolean{
        var status:Boolean = false
        if((ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED )
            || (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED )){

            status = true
        }
        return status
    }

    fun isLocationEnabled():Boolean{
        var status = false
        var locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)){
            status = true
        }
        return status
    }

    @SuppressLint("MissingPermission")
    fun getLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),

            object : LocationCallback(){
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    val location = p0.lastLocation
                    var lon = location?.longitude.toString()
                    var lat = location?.latitude.toString()


                    longitude = lon.toDouble()
                    latitude = lat.toDouble()
                    settings.setLatitude(latitude)
                    settings.setLongitude(longitude)
                    currentWeather.getWeather(lon.toDouble(), lat.toDouble() , settings.getLanguage() , settings.getUnit() , settings.getLocationMethod())


                    fusedLocationProviderClient.removeLocationUpdates(this)

                }
            },
            Looper.myLooper()
        )
    }

    fun convertToText(lat:Double , lon:Double){
        val latitude = lat
        val longitude = lon
        val geoCoder: Geocoder = Geocoder(requireContext())
        Log.i("TAG", "convertToText: 1" )
        if(latitude!=null && longitude!=null) {
            geoCoder.getFromLocation(longitude, latitude, 1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (!addresses.isEmpty()) {
                            Log.i("TAG", "convertToText: 2" )
                            val address = addresses.get(0)
                            var i = 0
                            Log.i("TAG", "convertToText: " + address.getAddressLine(i))
                            locationtext = address.getAddressLine(i)
                        }
                    }
                }
            )
        }
    }
    private fun checkConnectivity(){
        val connectivityManager =
            requireActivity().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected)){
            getWeatherByLocationType()
        }

    }
}

