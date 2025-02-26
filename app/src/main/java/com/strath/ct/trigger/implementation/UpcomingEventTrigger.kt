package com.strath.ct.trigger.implementation

import android.content.Intent
import android.net.Uri
import com.strath.ct.contextdata.IContextDataHolder
import com.strath.ct.contextdatasources.CalendarEvent
import com.strath.ct.trigger.SimpleTrigger
import java.util.*
import java.util.concurrent.TimeUnit


private const val MIN_MINUTES_THRESHOLD = 40
private const val MAX_MINUTES_THRESHOLD = 80

private const val TRIGGER_TITLE = "Upcoming Event Trigger"
private const val TRIGGER_TEXT = "You have an event today, why not walk ? Tap to see the route"

/**
 * The upcoming event trigger is a trigger for when the user has an event upcoming, indicating a
 * possibility for fitness activity by walking to the location this event is held.
 *
 */
class UpcomingEventTrigger (holder: IContextDataHolder) : SimpleTrigger(holder) {
    /**
     * The data source holder
     */
    private val mContextHolder: IContextDataHolder

    /**
     * The next event in the user's calendar.
     */
    private var mNextEvent: CalendarEvent? = null

    /**
     * Constructs a new SimpleTrigger with the given name.
     *
     * @param holder  the data source holder for accessing data
     */
    init {
        mContextHolder = holder
    }

    override val notificationTitle: String? = TRIGGER_TITLE

    override val notificationMessage: String
        get() {
            val cal = Calendar.getInstance()
            cal.timeInMillis = mNextEvent!!.startTime
            val time = cal[Calendar.HOUR_OF_DAY].toString() + ":" + cal[Calendar.MINUTE]
            val location: String? = mNextEvent?.location
            return String.format(TRIGGER_TEXT, time, location)
        }
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
    // Basic checking

    // Get the difference between times in minutes, shouldn't be negative but making sure.
    override val isTriggered: Boolean
        get() {
            var today: List<CalendarEvent>? = null
            if(mContextHolder.todayEvents != null) {
                today = mContextHolder.todayEvents as List<CalendarEvent>
            }

            // Basic checking
            if (today == null || today.isEmpty()) {
                return false
            }
            mNextEvent = today[0]
            val now = System.currentTimeMillis()
            val start: Long = mNextEvent!!.startTime

            // Get the difference between times in minutes, shouldn't be negative but making sure.
            val diff = TimeUnit.MILLISECONDS.toMinutes(Math.abs(start - now))
            return diff in MIN_MINUTES_THRESHOLD..MAX_MINUTES_THRESHOLD && mNextEvent!!.location != null
        }

}