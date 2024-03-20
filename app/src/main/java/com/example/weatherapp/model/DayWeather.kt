package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherapp.db.CityConverter
import com.example.weatherapp.db.DataListConverter
import com.google.gson.annotations.SerializedName


@Entity(tableName = "day_weather")
data class DayWeather(
    @PrimaryKey
    @TypeConverters(DataListConverter::class)
    var list: MutableList<Data> = mutableListOf() ,

    @TypeConverters(CityConverter::class)
    val city: City = City() ,

    var lang:String
)

class City{

    var name = ""
    var sunrise = 0
    var sunset = 0
    var coord:Coord = Coord()
}

class Coord{

    var lon:Double = 0.0
    var lat:Double = 0.0
}

class Main {
    val temp = 0.0

    val pressure = 0.0

    val feels_like = 0.0

    val temp_min = 0.0

    val temp_max = 0.0

    val sea_level = 0

    val grnd_level = 0

    val humidity = 0

    val temp_kf = 0.0

}

data class Data(
    var dt:Long ,
    var main:Main ,
    var weather:List<Weather> ,
    var clouds:Clouds ,
    var wind:Wind ,
    var visibility: Int ,
    var pop: Double ,
    var rain:Rain ,
    var dt_txt:String ,
    var snow:Snow  = Snow() ,
    var currentTime:Boolean = false

)


class Rain{
    @SerializedName("3h")
    var last_three_hours = 0.0
}


class Wind{
    var speed = 0.0
    var deg = 0
    var gust = 0.0
}

class Clouds{
    val all = 0
}

class Weather{
    var id:Int = 0
    var main:String = String()
    var description:String = String()
    var icon:String = String()
}

class Snow{
    @SerializedName("3h")
    var last_three_hours = 0.0
}