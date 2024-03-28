import android.app.Application
import android.util.Log
import com.example.weatherapp.data.local.setting.SettingLocalDataSource
import com.example.weatherapp.data.local.setting.SettingLocalDataSourceImpl

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
