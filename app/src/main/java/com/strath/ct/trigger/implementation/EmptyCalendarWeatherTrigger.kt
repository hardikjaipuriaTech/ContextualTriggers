package com.strath.ct.trigger.implementation

import android.content.Intent
import android.provider.CalendarContract
import com.strath.ct.contextdata.IContextDataHolder
import com.strath.ct.trigger.CompositeTrigger
import com.strath.ct.trigger.ITrigger

/**
 * The title of the notification.
 */
private const val TRIGGER_TITLE = "Empty Calendar & Good Weather Trigger"

/**
 * The text of this notification.
 */
private const val TRIGGER_TEXT = "You've nothing planned today, and its sunny outside, why not go for a walk?"

/**
 * The EmptyCalendarWeatherTrigger analyses the user's calendar and weather to determine whether
 * or not to suggest ot the user that should go outside and do some activity.
 *
 */
class EmptyCalendarWeatherTrigger(
    private val mTriggers: List<ITrigger>, holder: IContextDataHolder
) : CompositeTrigger(
    mTriggers, holder
) {
    /**
     * The data source containing information.
     */
    private val mContextHolder: IContextDataHolder

    /**
     * Constructs a new CompositeTrigger.
     *
     * @param triggers  the triggers that comprise of this trigger
     * @param holder  the data holder
     */
    init {
        mContextHolder = holder
    }

    override val notificationTitle: String?= TRIGGER_TITLE


    override val notificationIntent: Intent?
        get() {
        return Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, "Walking")
            .putExtra(CalendarContract.Events.DESCRIPTION, "Created by ContextualTriggers")
    }

    override val notificationMessage: String?
        get() {
        val weather: String = mContextHolder.weatherForecast?.forecast.toString()
        return String.format(TRIGGER_TEXT, weather)
    }

    override val isTriggered: Boolean?
        get() {
        var shouldTrigger = true
        for (trigger in mTriggers) {
            shouldTrigger = shouldTrigger && trigger.isTriggered!!
        }
        return shouldTrigger
    }
}