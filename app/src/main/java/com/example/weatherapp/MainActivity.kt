package com.example.weatherapp

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

     lateinit var drawerLayout:DrawerLayout
    var actionBar:ActionBar? = null
    lateinit var fragText:TextView
    lateinit var icon:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*val repo:Repository = Repository.Instance()

        lifecycleScope.launch(Dispatchers.IO) {

            val result = repo.getWeather(44.34 , 10.99)

            if(result.isSuccessful){

                Log.i("TAG", "onCreate: " + result.body()?.list?.get(0)?.dt_txt)

            }
            else{
                Log.i("TAG", "onCreate: " + result.errorBody())
            }

        }*/

        // intialize drawer
        drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        var navigationView = findViewById<NavigationView>(R.id.navigationItem)
        fragText = findViewById(R.id.fragText)
        icon = findViewById(R.id.drawerIcon)


        // create action bar
        /*val actionBar = supportActionBar
        actionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        actionBar?.setCustomView(R.layout.custom_action_bar)
        actionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background))
        val titleTextView = actionBar?.customView?.findViewById<TextView>(R.id.action_bar_title)



        // handle icon to open and close drawer
        val actionBarIcon = actionBar?.customView?.findViewById<ImageView>(R.id.imageView2)
        */icon.setOnClickListener {
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
                    fragText.text = "Favourites"
                }
                else if(destination.id == R.id.homeScreen){
                    fragText.text = "Home"
                }
                else if(destination.id == R.id.alertScreen){
                    fragText.text = "Alert"
                }
                else if(destination.id == R.id.settingsScreen){
                    fragText.text = "Settings"
                }

            }

        })


    }



}