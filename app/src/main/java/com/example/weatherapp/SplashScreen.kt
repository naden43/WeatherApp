package com.example.weatherapp

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreen : AppCompatActivity() {

    lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        lifecycleScope.launch {
            delay(1250)
            withContext(Dispatchers.Main){
                binding.brandName.visibility = View.VISIBLE
            }
            delay(2000)
            withContext(Dispatchers.Main)
            {
                val intent: Intent = Intent(
                    this@SplashScreen,
                    MainActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        }

    }
}