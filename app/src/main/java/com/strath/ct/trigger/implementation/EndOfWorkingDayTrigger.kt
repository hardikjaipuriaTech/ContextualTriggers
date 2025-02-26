package com.strath.ct.trigger.implementation

import android.content.Intent
import com.strath.ct.contextdata.IContextDataHolder
import com.strath.ct.trigger.SimpleTrigger
import java.util.*

class EndOfWorkingDayTrigger(holder: IContextDataHolder) : SimpleTrigger(holder) {

    private val mContextHolder: IContextDataHolder = holder
    private val mNotificationTitle: String = "End of Working Day Trigger"
    private val mNotificationMessage: String = "Its always good to have a walk after the end of working day"
    private var endOfDayTime: Long = 0

    override val notificationTitle: String?= mNotificationTitle
    override val notificationMessage: String? = mNotificationMessage
    override val notificationIntent: Intent? = null

    override val complexity: Int
        get() {
               return 12
        }

    override val isTriggered: Boolean?
        get() {
        endOfDayTime = mContextHolder.endOfDayTime
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = endOfDayTime
        val endHour = calendar[Calendar.HOUR_OF_DAY]
        calendar.time = Date()
        val currentHour = calendar[Calendar.HOUR_OF_DAY]
        return endHour - currentHour in 0..1
    }
}