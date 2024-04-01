package com.example.weatherapp.favourite.view

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.R
import com.example.weatherapp.data.local.dayWeather.DayWeatherLocalDataSourceImpl
import com.example.weatherapp.data.local.setting.SettingLocalDataSourceImpl
import com.example.weatherapp.data.model.FavouriteWeather
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.databinding.FragmentFavouriteScreenBinding
import com.example.weatherapp.db.WeatherDataBase
import com.example.weatherapp.favourite.viewModel.FavouriteViewModel
import com.example.weatherapp.favourite.viewModel.FavouriteViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

        checkConnectivity()
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

        favouriteAdapter = FavouriteAdapter(requireContext()  ,
        {fav->

            val dialog = Dialog(requireActivity())
            dialog.setContentView(R.layout.confirm_deletion)
            dialog.window!!
                .setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            dialog.window!!.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.custom_dialog))

            val okBtn = dialog.findViewById<Button>(R.id.confirmBtn)
            val cancelBtn = dialog.findViewById<Button>(R.id.cancelBtn)


            okBtn.setOnClickListener{
                favouriteViewModel.deleteFavourite(fav)
                dialog.dismiss()
            }

            cancelBtn.setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        },{
            val action = FavouriteScreenDirections.actionFavouriteScreenToFavDetails()
                action.latitude = it.lat.toString()
                action.longitude = it.lon.toString()
                Navigation.findNavController(binding.root).navigate(action)
        })

        binding.favPlacesListView.apply {
            adapter = favouriteAdapter
        }
        lifecycleScope.launch(Dispatchers.IO) {
            favouriteViewModel.favPlaces.collect{

                if(it.size>0) {
                    binding.emptyFav.visibility = View.GONE
                    Log.i("TAG", "onViewCreated:  collect list")
                    //var list = favouriteViewModel.getList(it)
                    withContext(Dispatchers.Main)
                    {
                        Log.i("TAG", "onViewCreated:  ${it.size}")
                        favouriteAdapter.submitList(it)
                    }
                }
                else{
                    withContext(Dispatchers.Main)
                    {
                        favouriteAdapter.submitList(it)
                        binding.emptyFav.visibility = View.VISIBLE

                    }
                }
            }
        }


        binding.addFavPlaceBtn.setOnClickListener {
            val action = FavouriteScreenDirections.actionFavouriteScreenToMapFragment2()
            action.id = 2
            Navigation.findNavController(binding.root).navigate(action)
        }

    }

    override fun onStart() {
        super.onStart()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val networkCallback: NetworkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {

                lifecycleScope.launch(Dispatchers.Main) {

                        binding.networkLayout.visibility = View.GONE
                        binding.favLayout.visibility = View.VISIBLE

                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                runBlocking {
                    withContext(Dispatchers.Main){

                        binding.networkLayout.visibility = View.VISIBLE
                        binding.favLayout.visibility = View.GONE

                    }
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
            binding.favLayout.visibility= View.GONE
        }

    }
}


