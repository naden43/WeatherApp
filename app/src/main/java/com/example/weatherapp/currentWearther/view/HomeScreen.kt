package com.example.weatherapp.currentWearther.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.currentWearther.viewModel.CurrectWeatherFactory
import com.example.weatherapp.currentWearther.viewModel.CurrentWeatherViewModel
import com.example.weatherapp.databinding.FragmentHomeScreenBinding
import com.example.weatherapp.model.Data
import com.example.weatherapp.model.Repository
import com.example.weatherapp.network.ApiStatus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeScreen : Fragment() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val REQUEST_CODE = 500
    lateinit var currentWeather: CurrentWeatherViewModel

    lateinit var binding: FragmentHomeScreenBinding

    var locationtext : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val factory = CurrectWeatherFactory(Repository.Instance())
        currentWeather = ViewModelProvider(requireActivity() , factory).get(CurrentWeatherViewModel::class.java)


        lifecycleScope.launch {
            currentWeather.weather.collect {result ->

                when(result){

                    is ApiStatus.Success -> {

                        withContext(Dispatchers.Main) {

                            var returnedData = result.data

                            var currentHourTimeStamp = currentWeather.getCurrentTimeStamp()


                            returnedData.list = currentWeather.reArrangeList(returnedData.list , currentHourTimeStamp)

                            returnedData.list.get(currentHourTimeStamp).currentTime = true

                            //download icon
                            var logo = returnedData.list.get(currentHourTimeStamp).weather.get(0).icon
                            Picasso.get().load("https://openweathermap.org/img/wn/${logo}@4x.png")
                                .into(binding.weatherImage)

                            // display my location
                            binding.locationTxt.text = currentWeather.getLocationText(locationtext)


                            // display the date
                            binding.timeTxt.text = currentWeather.getDateString(returnedData.list.get(currentHourTimeStamp).dt_txt)


                            binding.descriptionTxt.text =
                                returnedData.list.get(currentHourTimeStamp).weather.get(0).description

                            // i am now in which time stamp
                            binding.tempTxt.text =
                                returnedData.list.get(currentHourTimeStamp).main.temp.toString()

                            val weatherAdapter = DayWeatherAdapter(requireActivity())
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
                                returnedData.list.get(currentHourTimeStamp).main.pressure.toString() + " mbar"

                            binding.windTxt.text =
                                returnedData.list.get(currentHourTimeStamp).wind.speed.toString()

                        }
                    }
                    is ApiStatus.Failure -> {

                        // show view of notwerk conictivity
                    }
                    is ApiStatus.Loading -> {


                    }

                }



            }
        }


    }


    override fun onStart() {
        super.onStart()

        if (checkPermission()) {
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

                    currentWeather.getWeather(lon.toDouble() , lat.toDouble())

                    convertToText(location!!)
                    //convertToText(location!!)
                    fusedLocationProviderClient.removeLocationUpdates(this)

                }
            },
            Looper.myLooper()
        )
    }

    fun convertToText(location: Location){
        val latitude = location.latitude
        val longitude = location.longitude
        val geoCoder: Geocoder = Geocoder(requireActivity())
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
}

