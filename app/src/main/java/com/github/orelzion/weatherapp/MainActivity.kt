package com.github.orelzion.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.orelzion.weatherapp.model.ClimacellService
import com.github.orelzion.weatherapp.model.ForecastDay
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weatherDaysList = findViewById<RecyclerView>(R.id.weatherDaysList)
        val adapter = WeatherDaysAdapter()

        weatherDaysList.adapter = adapter

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.climacell.co/v3/")
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
        val climacellService = retrofit.create(ClimacellService::class.java)

        val forecastCall = climacellService.getForecast(
            lat = 31.7683,
            lon = 35.2137,
            unit_system = "si",
            fields = arrayOf("weather_code", "temp"),
            start_time = "now",
            apikey = "UEPgicRS4WQMaQ2CkidjLlqHr2RdMEPV"
        )

        forecastCall.enqueue(ForecastCallback())
    }

    inner class ForecastCallback: Callback<List<ForecastDay>> {
        override fun onResponse(
            call: Call<List<ForecastDay>>,
            response: Response<List<ForecastDay>>
        ) {
            Toast.makeText(this@MainActivity, response.toString(), Toast.LENGTH_LONG).show()
        }

        override fun onFailure(call: Call<List<ForecastDay>>, t: Throwable) {
            Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
        }

    }

    class WeatherDaysAdapter : RecyclerView.Adapter<WeatherDaysAdapter.WeatherDaysViewHolder>() {

        var weatherDays: List<WeatherDay> = listOf(
            WeatherDay(Date(), "Sunny", "22"),
            WeatherDay(Date(), "Cloudy", "18"),
            WeatherDay(Date(), "Sunny", "26"),
            WeatherDay(Date(), "Rainy", "16")
        )

        class WeatherDaysViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
            val condition: TextView = itemView.findViewById(R.id.condition)
            val dateView: TextView = itemView.findViewById(R.id.date)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherDaysViewHolder {
            return WeatherDaysViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.weather_day_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: WeatherDaysViewHolder, position: Int) {
            val weatherDay = weatherDays[position]

            holder.condition.text = "${weatherDay.degrees}°"
            holder.dateView.text = weatherDay.date.toString()

            holder.itemView.context.resources.getIntArray(R.array.weather_icons)
            when (weatherDay.condition) {
                "Sunny" -> holder.weatherIcon.setImageResource(R.drawable.ic_sun_line)
                "Rainy" -> holder.weatherIcon.setImageResource(R.drawable.ic_rainy_line)
                "Cloudy" -> holder.weatherIcon.setImageResource(R.drawable.ic_cloudy_line)
            }
        }

        override fun getItemCount(): Int {
            return weatherDays.size
        }
    }

    data class WeatherDay(val date: Date, val condition: String, val degrees: String)
}