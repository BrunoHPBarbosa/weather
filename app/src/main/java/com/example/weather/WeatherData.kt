package com.example.weather

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class CurrentWeatherResponse(
    val weather: List<WeatherData>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
)

data class FiveDayWeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<FiveDayWeatherItem>,
    val city: City
)

data class WeatherData(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)


data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int,
    val grnd_level: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Rain(
    @SerializedName("1h") val oneHour: Double
)

data class Clouds(
    val all: Int
)

data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class FiveDayWeatherItem(
    val dt: Long,
    val main: Main,
    val weather: List<WeatherData>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain?,
    val sys: Sys,
    val dt_txt: String
)

data class City(
    val id: Long,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class Coord(
    val lat: Double,
    val lon: Double
)

// Função para obter o dia da semana
fun obterDiaSemana(data: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = Date(data * 1000) // Convertendo segundos para milissegundos
    val calendar = Calendar.getInstance()
    date?.let { calendar.time = it }
    return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        ?: "Desconhecido"
}
