package com.example.weatherapp

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val settingFactory = SettingViewModelFactory(Repository.Instance(WeatherRemoteDataSourceImpl.Instance() , SettingLocalDataSourceImpl.getInstance(this) ))
        var settings = ViewModelProvider(this, settingFactory).get(SettingViewModel::class.java)


        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                var action = settings.language.collect {
                    val primaryLocale: Locale = this@MainActivity.resources.configuration.locales[0]
                    val locale: String = primaryLocale.language
                    Log.i("TAG", "onCreate: here $locale")
                    if(!locale.equals(it)) {
                        Log.i("TAG", "onCreate:  $it")
                        var local: Locale = Locale(it)
                        Locale.setDefault(local) // Set default locale
                        var resources: Resources = this@MainActivity.resources
                        var config: Configuration = resources.configuration
                        config.setLocale(local)
                        resources.updateConfiguration(config, resources.displayMetrics)

                        this@MainActivity.recreate()

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
                }
                else if(destination.id == R.id.homeScreen){
                    fragText.text = getString(R.string.home)
                }
                else if(destination.id == R.id.alertScreen){
                    fragText.text = getString(R.string.alert)
                }
                else if(destination.id == R.id.settingsScreen){
                    fragText.text = getString(R.string.settings)
                }

            }

        })
    }



}