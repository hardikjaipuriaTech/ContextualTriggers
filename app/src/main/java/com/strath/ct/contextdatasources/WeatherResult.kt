package com.strath.ct.contextdatasources

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * The weather result is an encapsulation of data obtained from any weather source. It provides
 * details on weather conditions such as temperature, wind or any other information a trigger may
 * wish to use for determining if the weather is sufficient for this trigger.
 *
 */
class WeatherResult(`in`: Parcel) : Parcelable {

    val forecast: WeatherForecastType = WeatherForecastType.valueOf(`in`.readString()!!)

    private val temperature: Double  = `in`.readDouble()

    private val humidity: Int = `in`.readInt()

    private val windSpeed: Double =`in`.readDouble()

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(forecast.name)
        dest.writeDouble(temperature)
        dest.writeInt(humidity)
        dest.writeDouble(windSpeed)
    }

    companion object {
        val TAG: String? =  "WeatherResult"

        @JvmField
        val CREATOR: Creator<WeatherResult?> = object : Creator<WeatherResult?> {
            override fun createFromParcel(`in`: Parcel): WeatherResult? {
                return WeatherResult(`in`)
            }

            override fun newArray(size: Int): Array<WeatherResult?> {
                return arrayOfNulls(size)
            }
        }
    }
}