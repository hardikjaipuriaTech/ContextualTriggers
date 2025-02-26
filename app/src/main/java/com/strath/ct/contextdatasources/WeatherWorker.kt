package com.strath.ct.contextdatasources

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.strath.ct.ContextDataManager
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

private const val WEATHER_TAG = "weather"
private const val ID_TAG = "id"
private const val MAIN_TAG = "main"
private const val TEMPERATURE_TAG = "temp"
private const val HUMIDITY_TAG = "humidity"
private const val WIND_TAG = "wind"
private const val WIND_SPEED_TAG = "speed"

class WeatherWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private val mContext: Context = context

    override fun doWork(): Result {

        val url = "$WEATHER_API_URL?lat=44.34&lon=10.99&appid=$API_KEY&units=metric"
        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(TAG, "Error fetching weather data", e)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val json = response.body?.string()
                val jsonObject = JSONObject(json)
                handleResult(jsonObject)
                Log.d(TAG, "Weather data: $json")
            }
        })

        return Result.success()
    }

    private fun handleResult(result: JSONObject) {
        try {
            val array: JSONArray = result.getJSONArray(WEATHER_TAG)
            val statusCode = array.getJSONObject(0).get(ID_TAG) as Int
            val forecast = convertWeatherId(statusCode)
            val main: JSONObject = result.getJSONObject(MAIN_TAG)
            val temperature = main.get(TEMPERATURE_TAG) as Double
            val humidity = main.get(HUMIDITY_TAG) as Int
            val wind: JSONObject = result.getJSONObject(WIND_TAG)
            val windSpeed = wind.get(WIND_SPEED_TAG) as Double
            forecast?.let { sendResult(it, temperature, humidity, windSpeed) }
        } catch (e: Exception) {
            sendResult(null, 0.0, 0, 0.0)
        }
    }

    private fun sendResult(
        forecast: WeatherForecastType?,
        temp: Double,
        humidity: Int,
        wind: Double
    ) {
        var result: WeatherResult? = null
        if (forecast != null) {
            val parcel: Parcel = Parcel.obtain()
            parcel.writeString(forecast.name)
            parcel.writeDouble(temp)
            parcel.writeInt(humidity)
            parcel.writeDouble(wind)
            parcel.setDataPosition(0)
            result = WeatherResult.CREATOR.createFromParcel(parcel)
            parcel.recycle()
        }
        println("Weather result: $result")
        val intent = Intent(mContext, ContextDataManager::class.java)
        intent.putExtra("DataSource", "Weather")
        intent.putExtra(WeatherResult.TAG, result)
        Log.d(WeatherWorker.TAG, "Intent ContextDataManager Service sent from WeatherWorker")
        mContext.startForegroundService(intent)
    }

    companion object {
        private const val TAG = "WeatherWorker"
        private const val WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather"
        private const val API_KEY = "72fea7c50a5622a959485e2731ce1f71"

        /**
         * Converts the given OpenWeatherMap id into a WeatherResult.
         *
         * @param id  the id to be converted
         * @return  the corresponding weather forecast.
         */
        fun convertWeatherId(id: Int): WeatherForecastType? {
            if (id in 200..299) return WeatherForecastType.THUNDERSTORM
            if (id in 300..399) return WeatherForecastType.DRIZZLE
            if (id in 500..599) return WeatherForecastType.RAIN
            if (id in 600..699) return WeatherForecastType.SNOW
            if (id == 800) return WeatherForecastType.CLEAR
            return if (id in 801..899) WeatherForecastType.CLOUDY else null
        }
    }
}