package com.strath.ct.trigger.implementation

import android.content.Intent
import com.strath.ct.contextdata.IContextDataHolder
import com.strath.ct.contextdatasources.WeatherForecastType
import com.strath.ct.contextdatasources.WeatherResult
import com.strath.ct.trigger.SimpleTrigger

private const val TriggerTitle = "Good Weather Trigger"

private const val TriggerMessage ="It's sunny outside, it shall be awesome to go for a walk"
/**
 * The GoodWeatherTrigger is a trigger that enacts when it deems that the current weather is
 * suitable for activity.
 *
 */
class GoodWeatherTrigger(private val mContextHolder: IContextDataHolder) : SimpleTrigger(mContextHolder) {
    /**
     * The current weather forecast.
     */
    private var mForecast: WeatherResult? = mContextHolder.weatherForecast
    override val notificationTitle: String? = TriggerTitle
    override val notificationMessage: String? = TriggerMessage
    override val notificationIntent: Intent? = null
    override val isTriggered: Boolean
        get() {
            return if (mForecast == null) false else when (mForecast!!.forecast) {
                WeatherForecastType.CLEAR, WeatherForecastType.CLOUDY -> true
                else -> false
            }
        }
}