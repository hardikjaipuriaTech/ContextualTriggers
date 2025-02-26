package com.strath.ct.trigger.implementation

import android.content.Intent
import android.net.Uri
import com.strath.ct.contextdata.IContextDataHolder
import com.strath.ct.contextdatasources.CalendarEvent
import com.strath.ct.contextdatasources.WeatherResult
import com.strath.ct.trigger.CompositeTrigger
import com.strath.ct.trigger.ITrigger
import java.text.SimpleDateFormat
import java.util.*

/**
 * The title of the notification.
 */
private const val NOTIFICATION_TITLE  = "Upcoming Event Weather Trigger"

/**
 * The text of this notification.
 */
private const val NOTIFICATION_TEXT = "Weather is good and you seem to be going out today, it would be great to walk to the event"

/**
 * The UpcomingEventWeatherTrigger combines the upcoming event and weather triggers, to ensure that
 * the upcoming event trigger only occurs when the weather is good.
 *
 */
class UpcomingEventWeatherTrigger (private val mTriggers: List<ITrigger>, holder: IContextDataHolder
) : CompositeTrigger(mTriggers, holder) {
    /**
     * The data source containing information.
     */
    private val mContextHolder: IContextDataHolder = holder

    /**
     * The upcoming event on the calendar.
     */
    private var mNextEvent: CalendarEvent? = null

    /**
     * The current weather forecast.
     */
    private var mForeCast: WeatherResult? = null

    override val notificationTitle: String? = NOTIFICATION_TITLE

    override val notificationIntent: Intent
        get() {
            val location: String? = mNextEvent?.location
            val locUri = Uri.encode(location)
            val baseUri = "google.navigation:q=%s&mode=w"
            val gmmIntentUri = Uri.parse(String.format(baseUri, locUri))
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            return mapIntent
        }
    override val notificationMessage: String
        get() {
            val cal = Calendar.getInstance()
            cal.timeInMillis = mNextEvent?.startTime!!
            val dateFormat = SimpleDateFormat("HH.mm")
            val time = dateFormat.format(cal.time)
            val location: String? = mNextEvent?.location
            val weather: String = mForeCast?.forecast.toString()
            return String.format(NOTIFICATION_TEXT, time, weather, location)
        }

    override val isTriggered: Boolean
        get() {
            var shouldTrigger = true
            for (trigger in mTriggers) {
                shouldTrigger = shouldTrigger && trigger.isTriggered!!
            }

            // Basic checking
            if (shouldTrigger) {
                val today: List<CalendarEvent> = mContextHolder.todayEvents as List<CalendarEvent>
                mNextEvent = today[0]
                mForeCast = mContextHolder.weatherForecast
            }
            return shouldTrigger
        }
}