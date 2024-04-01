package com.example.weatherapp

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.databinding.FragmentMapBinding
import com.example.weatherapp.data.local.dayWeather.DayWeatherLocalDataSourceImpl
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.data.local.setting.SettingLocalDataSourceImpl
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.db.WeatherDataBase
import com.example.weatherapp.settings.viewModel.SettingViewModel
import com.example.weatherapp.settings.viewModel.SettingViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.util.Locale
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class mapFragment : Fragment()  , OnMapReadyCallback{



    private var googleMap:GoogleMap? = null
    lateinit var binding:FragmentMapBinding
    var latitude : Double? = null
    var longitude: Double? = null
    var marker: Marker? = null
    var country:String? = null
    lateinit var settings : SettingViewModel
    lateinit var lang:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingFactory = SettingViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity()) , DayWeatherLocalDataSourceImpl.getInstance(
            WeatherDataBase.getInstance(requireContext()).getDayWeatherDao() ,
            WeatherDataBase.getInstance(requireContext()).getFavouriteDao() , WeatherDataBase.getInstance(requireContext()).getAlertWeatherDao() ) ))
        settings = ViewModelProvider(requireActivity() , settingFactory).get(SettingViewModel::class.java)

        lang = settings.getLanguage()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(layoutInflater , container , false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addLocationBtn.isEnabled = false
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment?.getMapAsync(this)

        binding.addLocationBtn.setOnClickListener {
            if(latitude!=null && longitude!=null)
            {
                lifecycleScope.launch(Dispatchers.IO){

                    if(arguments?.getInt("id") == 1) {
                        settings.deleteDataBase()
                        settings.setSession(false)
                        withContext(Dispatchers.Main) {
                            settings.setLatitude(latitude!!)
                            settings.setLongitude(longitude!!)
                            val action = mapFragmentDirections.actionMapFragment2ToHomeScreen()
                            action.latitiude = latitude!!.toFloat()
                            action.longitiude = longitude!!.toFloat()
                            Navigation.findNavController(binding.root).navigate(action)
                        }
                    }
                    else if(arguments?.getInt("id") == 2)
                    {
                        country = getCountryName(latitude!!, longitude!!)
                        withContext(Dispatchers.Main) {
                            val action = mapFragmentDirections.actionMapFragment2ToFavouriteScreen()
                            action.longitude = longitude!!.toString()
                            action.latitude = latitude!!.toString()
                            action.country = country?:"UnKnown"
                            Navigation.findNavController(binding.root).navigate(action)
                        }

                    }
                    else if(arguments?.getInt("id") == 3)
                    {
                        withContext(Dispatchers.Main) {
                            val action = mapFragmentDirections.actionMapFragment2ToAlertScreen()
                            action.longitude = longitude!!.toString()
                            action.latitude = latitude!!.toString()
                            Navigation.findNavController(binding.root).navigate(action)
                        }
                    }
                }
            }
        }

        binding.backButton.setOnClickListener{
            //requireFragmentManager().popBackStack()
            val id = arguments?.getInt("id")
            if(id == 1)
            {
                val action = mapFragmentDirections.actionMapFragment2ToSettingsScreen()
                Navigation.findNavController(binding.root).navigate(action)
            }
            else if(id == 2)
            {
                val action = mapFragmentDirections.actionMapFragment2ToFavouriteScreen()
                action.longitude = "0.0"
                action.latitude = "0.0"
                action.country = ""
                Navigation.findNavController(binding.root).navigate(action)
            }
            else if(id ==3 )
            {
                val action = mapFragmentDirections.actionMapFragment2ToAlertScreen()
                action.longitude = "0.0"
                action.latitude = "0.0"
                Navigation.findNavController(binding.root).navigate(action)
            }
        }
    }


    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        googleMap!!.setOnMapClickListener { latLng ->
            latitude = latLng.latitude
            longitude = latLng.longitude



            marker?.remove()
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    binding.addLocationBtn.isEnabled = true
                }
            }
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title("Clicked Location")
                .snippet("Latitude: $latitude, Longitude: $longitude")
            marker = googleMap!!.addMarker(markerOptions)
        }
    }



    suspend fun getCountryName(latitude: Double, longitude: Double): String {
        val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude&accept-language=${settings.getLanguage()}"

        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                val responseBody = response.body()?.string()

                try {
                    val jsonObject = JSONObject(responseBody)
                    val address = jsonObject.optJSONObject("address")
                    val country = address?.optString("country", "UnKnown")
                    val city = jsonObject.optString("name", "UnKnown")
                    city + " " + country
                } catch (e: JSONException) {
                    e.printStackTrace()
                    "UnKnown"
                }
            } catch (e: IOException) {
                e.printStackTrace()
                "UnKnown"
            }
        }
    }


}

