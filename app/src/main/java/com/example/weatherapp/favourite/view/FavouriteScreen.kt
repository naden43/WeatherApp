package com.example.weatherapp.favourite.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.databinding.FragmentFavouriteScreenBinding
import com.example.weatherapp.favourite.viewModel.FavouriteViewModel
import com.example.weatherapp.favourite.viewModel.FavouriteViewModelFactory
import com.example.weatherapp.data.local.dayWeather.DayWeatherLocalDataSourceImpl
import com.example.weatherapp.data.model.FavouriteWeather
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.data.local.setting.SettingLocalDataSourceImpl
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.db.WeatherDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteScreen : Fragment() {

    lateinit var binding: FragmentFavouriteScreenBinding
    lateinit var favouriteViewModel: FavouriteViewModel
    lateinit var favouriteAdapter: FavouriteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteScreenBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = FavouriteViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity()) , DayWeatherLocalDataSourceImpl.getInstance(
            WeatherDataBase.getInstance(requireContext()).getDayWeatherDao() ,
            WeatherDataBase.getInstance(requireContext()).getFavouriteDao() , WeatherDataBase.getInstance(requireContext()).getAlertWeatherDao() ) ))
        favouriteViewModel = ViewModelProvider(requireActivity() , factory).get(FavouriteViewModel::class.java)

        if (arguments != null)
        {
             var latitude = arguments?.getString("latitude")?.toDouble() ?: 0.0
             var longitude =  arguments?.getString("longitude")?.toDouble() ?: 0.0
             var country = arguments?.getString("country").toString()

            if(latitude!=0.0 && longitude!=0.0) {
                var favouriteWeather = FavouriteWeather(country , latitude , longitude)
                favouriteViewModel.addFavourite(favouriteWeather)
                arguments = null
            }
        }

        favouriteAdapter = FavouriteAdapter(requireContext())
        {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("delete favourite place")
            builder.setMessage("Are you sure you want to delete ?")
            var dialog : AlertDialog? = null
            builder.setPositiveButton("Yes") { dialog,which->
                favouriteViewModel.deleteFavourite(it)
            }

            builder.setNegativeButton("No") { dialog, which ->

            }
            dialog = builder.create()
            dialog.show()
        }
        binding.favPlacesListView.apply {
            adapter = favouriteAdapter
        }
        lifecycleScope.launch(Dispatchers.IO) {
            favouriteViewModel.favPlaces.collect{

                Log.i("TAG", "onViewCreated:  collect list")
                //var list = favouriteViewModel.getList(it)
                withContext(Dispatchers.Main)
                {
                    Log.i("TAG", "onViewCreated:  ${it.size}")
                    favouriteAdapter.submitList(it)
                }
            }
        }


        binding.addFavPlaceBtn.setOnClickListener {
            val action = FavouriteScreenDirections.actionFavouriteScreenToMapFragment2()
            action.id = 2
            Navigation.findNavController(binding.root).navigate(action)
        }

    }


}