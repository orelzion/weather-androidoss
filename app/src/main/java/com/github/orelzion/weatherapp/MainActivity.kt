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
import com.github.orelzion.weatherapp.model.WeatherCondition
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class MainActivity : AppCompatActivity() {

    private var weatherDaysAdapter: WeatherDaysAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weatherDaysList = findViewById<RecyclerView>(R.id.weatherDaysList)
        weatherDaysAdapter = WeatherDaysAdapter()

        weatherDaysList.adapter = weatherDaysAdapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.climacell.co/v3/")
            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
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

    inner class ForecastCallback : Callback<List<ForecastDay>> {
        override fun onResponse(
            call: Call<List<ForecastDay>>,
            response: Response<List<ForecastDay>>
        ) {
            if (response.isSuccessful) {
                val weatherDays = response.body()?.map { forecastDay: ForecastDay ->
                    WeatherDay(
                        dayOfWeek = forecastDay.observation_time.toCalendar()
                            .get(Calendar.DAY_OF_WEEK),
                        condition = forecastDay.weather_code.value,
                        degrees = forecastDay.getAverageTemp().formatted()
                    )
                }

                weatherDays?.let {
                    weatherDaysAdapter?.weatherDays = it
                    weatherDaysAdapter?.notifyDataSetChanged()
                }

            } else {
                //TODO handle error
            }
        }

        override fun onFailure(call: Call<List<ForecastDay>>, t: Throwable) {
            Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
        }

    }

    class WeatherDaysAdapter : RecyclerView.Adapter<WeatherDaysAdapter.WeatherDaysViewHolder>() {

        var weatherDays: List<WeatherDay> = emptyList()

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

            holder.condition.text = weatherDay.degrees
            holder.dateView.text =
                holder.itemView.context.resources.getStringArray(R.array.days_of_week)[weatherDay.dayOfWeek - 1]

            holder.itemView.context.resources.getIntArray(R.array.weather_icons)

            holder.weatherIcon.setImageResource(
                when (weatherDay.condition) {
                    WeatherCondition.rain_heavy,
                    WeatherCondition.rain,
                    WeatherCondition.rain_light,
                    WeatherCondition.freezing_rain_heavy,
                    WeatherCondition.freezing_rain,
                    WeatherCondition.freezing_rain_light,
                    WeatherCondition.freezing_drizzle,
                    WeatherCondition.drizzle -> R.drawable.ic_rainy_line
                    WeatherCondition.ice_pellets_heavy -> TODO()
                    WeatherCondition.ice_pellets -> TODO()
                    WeatherCondition.ice_pellets_light -> TODO()
                    WeatherCondition.snow_heavy -> TODO()
                    WeatherCondition.snow -> TODO()
                    WeatherCondition.snow_light -> TODO()
                    WeatherCondition.flurries -> TODO()
                    WeatherCondition.tstorm -> TODO()
                    WeatherCondition.fog_light -> TODO()
                    WeatherCondition.fog -> TODO()
                    WeatherCondition.cloudy,
                    WeatherCondition.mostly_cloudy,
                    WeatherCondition.partly_cloudy -> R.drawable.ic_cloudy_line
                    WeatherCondition.mostly_clear,
                    WeatherCondition.clear -> R.drawable.ic_sun_line
                }
            )
        }

        override fun getItemCount(): Int {
            return weatherDays.size
        }
    }

    data class WeatherDay(val dayOfWeek: Int, val condition: WeatherCondition, val degrees: String)
}