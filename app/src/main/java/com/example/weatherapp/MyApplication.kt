import android.app.Application
import android.util.Log
import com.example.weatherapp.model.SettingLocalDataSource
import com.example.weatherapp.model.SettingLocalDataSourceImpl

class MyApplication : Application() {

    lateinit var localDataSourceImpl: SettingLocalDataSource // Late initialization

    override fun onCreate() {
        super.onCreate()
        localDataSourceImpl = SettingLocalDataSourceImpl.getInstance(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.i("TAG", "onTerminate: close session")
        localDataSourceImpl.setSession(false)
    }
}
