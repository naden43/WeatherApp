package com.example.weatherapp.alert.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.R
import com.example.weatherapp.alert.viewModel.AlertViewModel
import com.example.weatherapp.alert.viewModel.AlertViewModelFactory
import com.example.weatherapp.databinding.FragmentAlertScreenBinding
import com.example.weatherapp.data.local.dayWeather.DayWeatherLocalDataSourceImpl
import com.example.weatherapp.data.repository.Repository
import com.example.weatherapp.data.local.setting.SettingLocalDataSourceImpl
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import com.example.weatherapp.db.WeatherDataBase
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Calendar

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

        val factory = AlertViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(requireActivity()) , DayWeatherLocalDataSourceImpl.getInstance(WeatherDataBase.getInstance(requireContext()).getDayWeatherDao() ,WeatherDataBase.getInstance(requireContext()).getFavouriteDao() , WeatherDataBase.getInstance(requireContext()).getAlertWeatherDao() ) ))
        alertViewModel = ViewModelProvider(requireActivity() , factory).get(AlertViewModel::class.java)

        checkConnectivity()
        if (arguments != null)
        {
            var latitude = arguments?.getString("latitude")?.toDouble() ?: 0.0
            var longitude =  arguments?.getString("longitude")?.toDouble() ?: 0.0

            if(latitude!=0.0 && longitude!=0.0) {

                val dialog = Dialog(requireActivity())
                var action = 1
                dialog.setContentView(R.layout.alert_dialog)
                dialog.window!!
                    .setLayout(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                dialog.window!!.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.custom_dialog))
                //val sigin = dialog.findViewById<Button>(R.id.sigin)
                val timeTxt = dialog.findViewById<TextView>(R.id.fromTimeTxt)
                val dateTxt = dialog.findViewById<TextView>(R.id.fromDateTxt)
                val cancelBtn = dialog.findViewById<Button>(R.id.cancelBtn)
                val okBtn = dialog.findViewById<Button>(R.id.okBtn)
                val layout = dialog.findViewById<ConstraintLayout>(R.id.fromConstrainLayout)
                val groupBtn = dialog.findViewById<RadioGroup>(R.id.notifyMethod)
                val notification = dialog.findViewById<RadioButton>(R.id.notificationBtn)
                okBtn.isEnabled = false

                groupBtn.setOnCheckedChangeListener { group, checkedId ->

                    okBtn.isEnabled = true
                    if(checkedId == R.id.notificationBtn)
                    {
                        action = 1
                        val notificationManager = requireActivity().getSystemService(NotificationManager::class.java) as NotificationManager
                        if(notificationManager.areNotificationsEnabled()){

                        }
                        else
                        {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                100
                            )
                        }
                    }
                    else{
                        action = 2
                        if(!Settings.canDrawOverlays(requireContext()))
                        {
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package: com.example.weatherapp"))
                            startActivityForResult(intent, 120)
                        }
                    }
                }



                val calendar = Calendar.getInstance()
                val year = calendar[Calendar.YEAR]
                val mounth = calendar[Calendar.MONTH]
                val day = calendar[Calendar.DAY_OF_MONTH]
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)


                var date : String = "$year-${String.format("%02d", mounth)}-${String.format("%02d", day)}"
                var time : String = "${String.format("%02d", hour)}:${String.format("%02d", minute)}"

                val start = calendar.timeInMillis

                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { view, year, month, dayOfMonth ->


                    date = "$year-${month + 1}-$dayOfMonth"

                    val isSystem24Hours  = is24HourFormat(requireContext())
                    val clockFormat = when(isSystem24Hours){
                        true -> TimeFormat.CLOCK_24H
                        else -> TimeFormat.CLOCK_12H
                    }

                    val picker = MaterialTimePicker.Builder()
                        .setTimeFormat(clockFormat)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Alarm Time")
                        .build()

                    picker.show(childFragmentManager , "TAG")
                     picker.addOnPositiveButtonClickListener {

                         time= "${String.format("%02d", picker.hour)}:${String.format("%02d", picker.minute)}"
                         timeTxt.text = time
                         dateTxt.text = date
                         dialog.show()

                     }

                    }, year, mounth, day)
                datePickerDialog.setOnDismissListener {
                    Log.i("TAG", "onViewCreated: dismiss")
                }
                datePickerDialog.datePicker.minDate = start
                datePickerDialog.show()


                okBtn.setOnClickListener {
                    alertViewModel.addAlert(latitude, longitude , date , time , action , requireContext())
                    dialog.dismiss()
                }
                cancelBtn.setOnClickListener {
                    dialog.dismiss()
                }
                arguments = null
            }
        }

        alertAdapter = AlertAdapter(requireContext())
        {alert ->
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
                alertViewModel.deleteAlert(alert)
                dialog.dismiss()
            }

            cancelBtn.setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            if(grantResults.size==1){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                    }
                }
            }
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

                runBlocking {
                    withContext(Dispatchers.Main) {
                        binding.networkLayout.visibility = View.GONE
                        binding.alertLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                runBlocking{
                    withContext(Dispatchers.Main)
                    {
                        binding.networkLayout.visibility = View.VISIBLE
                        binding.alertLayout.visibility= View.GONE
                    }
                }

            }

            override fun onUnavailable() {
                super.onUnavailable()
                runBlocking{
                    withContext(Dispatchers.Main)
                    {
                        Log.i("TAG", "onLost: nnnn ")
                        binding.networkLayout.visibility = View.VISIBLE
                        binding.alertLayout.visibility= View.GONE
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
            binding.alertLayout.visibility= View.GONE
        }

    }
}