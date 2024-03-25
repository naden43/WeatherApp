package com.example.weatherapp

import android.app.Activity
import android.app.LocaleManager
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.weatherapp.currentWearther.viewModel.CurrectWeatherFactory
import com.example.weatherapp.currentWearther.viewModel.CurrentWeatherViewModel
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.DayWeatherLocalDataSourceImpl
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.SettingLocalDataSourceImpl
import com.example.weatherapp.network.WeatherRemoteDataSourceImpl
import com.example.weatherapp.settings.viewModel.SettingViewModel
import com.example.weatherapp.settings.viewModel.SettingViewModelFactory
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity() {

     lateinit var drawerLayout:DrawerLayout
    var actionBar:ActionBar? = null
    lateinit var fragText:TextView
    lateinit var icon:ImageView
    lateinit var binding:ActivityMainBinding
    lateinit var settings:SettingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingFactory = SettingViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(this) , DayWeatherLocalDataSourceImpl(this) ))
        settings = ViewModelProvider(this, settingFactory).get(SettingViewModel::class.java)

        Log.i("TAG", "onCreate:  ${settings.getSession()} ")
        if(savedInstanceState != null)
        {
            Log.i("TAG", "onCreate:  bundle save ")
            settings.setSession(true)
        }

        val primaryLocale: Locale = resources.configuration.locales[0]
        val locale: String = primaryLocale.language

       if(!locale.equals(settings.getLanguage())) {
           Log.i("TAG", "onCreate: jjjjjjjjjjjjjjjj${settings.getSession()} ")
             changeLanguage()
       }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO){

             repeatOnLifecycle(Lifecycle.State.STARTED) {
                settings.language.collect {

                    val primaryLocale: Locale = this@MainActivity.resources.configuration.locales[0]
                    val locale: String = primaryLocale.language

                    if (!locale.equals(it)) {
                        Log.i("TAG", "onCreate:  here  ${settings.getSession()}")
                        changeLanguage()
                        withContext(Dispatchers.Main) {
                            this@MainActivity.recreate()
                        }
                    }
                }

            }
        }




        // intialize drawer
        drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        var navigationView = findViewById<NavigationView>(R.id.navigationItem)
        fragText = findViewById(R.id.fragText)
        icon = findViewById(R.id.drawerIcon)


      icon.setOnClickListener {
            if(drawerLayout.isDrawerOpen(GravityCompat.START))
            {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            else
            {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        // connect nav controller
        val navController = findNavController(this, R.id.nav_host_fragment)
        setupWithNavController(navigationView, navController)


        // change the action bar title
        navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener{
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                if(destination.id == R.id.favouriteScreen){
                    fragText.text = getString(R.string.favourites)
                    this@MainActivity.binding.drawerIcon.visibility = View.VISIBLE
                    this@MainActivity.binding.fragText.visibility = View.VISIBLE
                    this@MainActivity.binding.icon.visibility = View.VISIBLE
                }
                else if(destination.id == R.id.homeScreen){
                    fragText.text = getString(R.string.home)
                    this@MainActivity.binding.drawerIcon.visibility = View.VISIBLE
                    this@MainActivity.binding.fragText.visibility = View.VISIBLE
                    this@MainActivity.binding.icon.visibility = View.VISIBLE
                }
                else if(destination.id == R.id.alertScreen){
                    fragText.text = getString(R.string.alert)
                    this@MainActivity.binding.drawerIcon.visibility = View.VISIBLE
                    this@MainActivity.binding.fragText.visibility = View.VISIBLE
                    this@MainActivity.binding.icon.visibility = View.VISIBLE
                }
                else if(destination.id == R.id.settingsScreen){
                    fragText.text = getString(R.string.settings)
                    this@MainActivity.binding.drawerIcon.visibility = View.VISIBLE
                    this@MainActivity.binding.fragText.visibility = View.VISIBLE
                    this@MainActivity.binding.icon.visibility = View.VISIBLE
                }
                else{
                    this@MainActivity.binding.drawerIcon.visibility = View.GONE
                    this@MainActivity.binding.fragText.visibility = View.GONE
                    this@MainActivity.binding.icon.visibility = View.GONE
                }

            }

        })
    }

    fun changeLanguage()
    {
        Log.i("TAG", "onCreate:  ${settings.getLanguage()}")
        var local: Locale = Locale(settings.getLanguage())
        Locale.setDefault(local) // Set default locale
        var resources: Resources = this@MainActivity.resources
        var config: Configuration = resources.configuration
        config.setLocale(local)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("TAG", "onDestroy:  finish")
        settings.setSession(false)
    }
}