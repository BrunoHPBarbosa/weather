package com.example.weather

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapService {
    @GET("geo/1.0/direct")
    suspend fun obterCoordenadas(
        @Query("q") cidade: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") appId: String
    ): Response<List<JsonObject>>

    @GET("data/2.5/weather")
    suspend fun obterDadosClimaAtual(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<CurrentWeatherResponse>

    @GET("/data/2.5/forecast")
    suspend fun obterPrevisaoSemanal(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<FiveDayWeatherResponse>
}



