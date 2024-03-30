package com.example.weatherapp.alert.viewModel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherapp.R
import com.example.weatherapp.data.model.DayWeather
import com.example.weatherapp.network.ApiService
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import com.example.weatherapp.db.WeatherDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory





class AlertWorker(context: Context , parameters: WorkerParameters): Worker(context , parameters){

    lateinit var mediaPlayer:MediaPlayer
    override fun doWork(): Result {

        Log.i("TAG", "doWork:  start work")
        val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        val appid:String = "936e138c87bad11b4cc706b7849cf427"

        // 1- get the data from api ( lat , long , lang )
             // 1- get object from remote data source

        val remote = WeatherRemoteDataSourceImpl.Instance()
        val dao = WeatherDataBase.getInstance(applicationContext).getAlertWeatherDao()
            // 2- get the weather from api
        val lon = inputData.getDouble(Constants.LONGITUDE , 0.0)
        val lat = inputData.getDouble(Constants.LATITUDE , 0.0)
        val lang = inputData.getString(Constants.LANGUAGE) ?: "en"
        val actionType = inputData.getInt(Constants.ACTION , 1)

        Log.i("TAG", "doWork: $lon , $lat , $lang , $actionType")

       val  retrofit =  Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(
            BASE_URL
        ).build()
       val api = retrofit.create(ApiService::class.java)

        var response : Response<DayWeather> ? = null
        runBlocking {
            response =  api.getWeather(lat, lon, lang, appid)
        }

        if(response!!.isSuccessful)
        {
            Log.i("TAG", "doWork: 1 ")
            val data = response?.body()
            if (data == null)
            {
                Log.i("TAG", "doWork: 2 ")
                return Result.failure()
            }
            else
            {
                if(actionType == 1)
                {
                    // notification
                    Log.i("TAG", "doWork: 3")

                    if(checkForNotificationPermission(applicationContext))
                    {
                         createNotification(data.list[0].weather.get(0).description)
                        return Result.success()
                    }
                    else
                    {
                        Log.i("TAG", "doWork: no permission...")
                       return Result.failure()
                    }

                }
                else
                {
                    Log.i("TAG", "doWork: 4 ")

                    if(Settings.canDrawOverlays(applicationContext)) {
                        Log.i("TAG", "doWork: alarm enter ")

                        runBlocking {
                            withContext(Dispatchers.Main) {
                                showOverlayDialog()
                            }
                        }
                        return  Result.success()
                    }
                    else{
                        return Result.failure()
                        Log.i("TAG", "doWork: alarm no permission ")
                    }

                }
            }
        }
        else
        {
            return  Result.failure()
        }


        //dao.deleteAlert()
    }

    fun createNotification(discription : String){


        val notificationId = 123

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Weather Channel"
            val importance  = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID , name ,importance)

            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val build = NotificationCompat.Builder(applicationContext , Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.alert)
            .setContentTitle("Weather APP")
            .setContentText(discription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true).build()
        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java) as NotificationManager

        notificationManager.notify(notificationId , build)
    }

    fun checkForNotificationPermission(context: Context):Boolean
    {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showOverlayDialog() {
        val alertDialog = AlertDialog.Builder(applicationContext)
            .setTitle("Alert")
            .setMessage("This is an overlay dialog!")
            .setPositiveButton("Dismiss") { dialog, _ ->
                stopSound()
                dialog.dismiss()
            }
            .create()

        // Set flags to make the dialog appear on top of other apps
        alertDialog.window?.apply {
            setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            setGravity(Gravity.TOP) // Set gravity to top
            attributes.y = 100 // Adjust the y-coordinate to position the dialog
        }


        // Show the dialog
        alertDialog.show()
        playSound()
    }

    private fun playSound() {
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.alert_dialog_sound)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun stopSound()
    {
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

}