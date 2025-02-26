package com.strath.ct.trigger.implementation

import android.content.Intent
import com.strath.ct.contextdata.IContextDataHolder
import com.strath.ct.contextdatasources.CalendarEvent
import com.strath.ct.trigger.SimpleTrigger

/**
 * The title of the notification.
 */
private const val NOTIFICATION_TITLE = "Empty Calendar Trigger"

/**
 * The text of this notification.
 */
private const val NOTIFICATION_TEXT =
    "There are no events planned in your calendar for today, maybe plan to go for a walk?"

/**
 * The trigger is for event the calendar indicates that today is empty.
 *
 */
class EmptyCalendarTrigger(holder: IContextDataHolder) : SimpleTrigger(holder) {

    private val mContextHolder: IContextDataHolder = holder

    override val notificationTitle: String? = NOTIFICATION_TITLE

    override val notificationMessage: String? = NOTIFICATION_TEXT

    override val notificationIntent: Intent? = null

    override val isTriggered: Boolean
        get() {
            return if (mContextHolder.todayEvents != null) {
                val events: List<CalendarEvent> = mContextHolder.todayEvents as List<CalendarEvent>
                events != null && events.isEmpty()
            } else {
                false
            }
        }
}