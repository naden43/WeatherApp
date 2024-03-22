package com.example.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.databinding.FragmentMapBinding
import com.example.weatherapp.model.DayWeatherLocalDataSourceImpl
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.SettingLocalDataSourceImpl
import com.example.weatherapp.network.WeatherRemoteDataSourceImpl
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

class mapFragment : Fragment()  , OnMapReadyCallback{



    private var googleMap:GoogleMap? = null
    lateinit var binding:FragmentMapBinding
    var latitude : Double? = null
    var longitude: Double? = null
    var marker: Marker? = null
    lateinit var settings : SettingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingFactory = SettingViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity()) , DayWeatherLocalDataSourceImpl.getInstance(requireActivity()) ))
        settings = ViewModelProvider(requireActivity() , settingFactory).get(SettingViewModel::class.java)

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

                    settings.deleteCashedData()
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
            }
        }

        binding.backButton.setOnClickListener{
            requireFragmentManager().popBackStack()
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





}