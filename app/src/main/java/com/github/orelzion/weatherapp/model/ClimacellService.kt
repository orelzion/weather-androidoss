package com.github.orelzion.weatherapp.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ClimacellService {
    @GET("weather/forecast/daily")
    fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("unit_system") unit_system: String,
        @Query("fields") fields: Array<String>,
        @Query("start_time") start_time: String,
        @Query("apikey") apikey: String
    ): Call<List<ForecastDay>>
}