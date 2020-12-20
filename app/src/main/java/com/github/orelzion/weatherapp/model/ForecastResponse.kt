package com.github.orelzion.weatherapp.model

import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(val forecastDays: List<ForecastDay>)

@Serializable
data class ForecastDay(
    val lat: Double,
    val lon: Double,
    val observation_time: ObservationTime,
    val weather_code: WeatherCode,
    val temp: List<Temperature>
)

@Serializable
data class ObservationTime(val value: String)
@Serializable
data class WeatherCode(val value: String)
@Serializable
data class Temperature(val observation_time: String, val min: TemperatureValue? = null, val max: TemperatureValue? = null)
@Serializable
data class TemperatureValue(val value: Double, val units: String)


