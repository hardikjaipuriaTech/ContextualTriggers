package com.strath.ct.trigger

import android.content.Context
import android.util.Log
import com.strath.ct.contextdata.IContextDataHolder
import com.strath.ct.notifications.frequencyCheck
import com.strath.ct.notifications.handleNotifications
import com.strath.ct.notifications.time
import com.strath.ct.trigger.implementation.*

class TriggerManager(context: Context, holder: IContextDataHolder) {
    private val mTriggers: MutableList<ITrigger>
    private val mContext: Context
    private val mContextHolder: IContextDataHolder

    //One notification every hour
    private val frequency = 1.0
    private var lastNotificationTime = 0.0
    private val lastNotificationTriggers: MutableList<ITrigger>

    init {
        Log.d("TriggerManager", "Initialized")
        mContextHolder = holder
        mContext = context
        mTriggers = ArrayList()
        lastNotificationTriggers = ArrayList()
    }

    fun manageTriggers() {

        //Initializing  Simple Triggers
        val goodWeatherTrigger: ITrigger = GoodWeatherTrigger(mContextHolder)
        val endOfWorkingDayTrigger: ITrigger = EndOfWorkingDayTrigger(mContextHolder)
        val batteryTrigger: ITrigger = BatteryTrigger(mContextHolder)
        val emptyCalendarTrigger: ITrigger = EmptyCalendarTrigger(mContextHolder)
        val stepsTrigger: ITrigger = StepsTrigger(mContextHolder)
        val upcomingEventTrigger: ITrigger = UpcomingEventTrigger(mContextHolder)

        val emptyCalendarWeatherTriggers: MutableList<ITrigger> = ArrayList()
        emptyCalendarWeatherTriggers.add(emptyCalendarTrigger)
        emptyCalendarWeatherTriggers.add(goodWeatherTrigger)

        val upcomingEventWeatherTriggerList: MutableList<ITrigger> = ArrayList()
        upcomingEventWeatherTriggerList.add(upcomingEventTrigger)
        upcomingEventWeatherTriggerList.add(goodWeatherTrigger)

        // Initializing Composite Triggers
        val emptyCalendarWeatherTrigger: ITrigger = EmptyCalendarWeatherTrigger(
            emptyCalendarWeatherTriggers,
            mContextHolder
        )
        val upcomingEventWeatherTrigger: ITrigger = UpcomingEventWeatherTrigger(
            upcomingEventWeatherTriggerList,
            mContextHolder
        )

        //Triggers
        mTriggers.add(batteryTrigger)
        mTriggers.add(goodWeatherTrigger)
        mTriggers.add(stepsTrigger)
        mTriggers.add(endOfWorkingDayTrigger)
        mTriggers.add(emptyCalendarTrigger)
        mTriggers.add(upcomingEventTrigger)

        mTriggers.add(emptyCalendarWeatherTrigger)
        mTriggers.add(upcomingEventWeatherTrigger)

        // Only Show Notification during active day time. Morning 9 am to 10 pm
        if (time > 9 && time < 22) {
            val activatedTriggers: MutableList<ITrigger> = ArrayList()
            for (t in mTriggers) {
                if (t.isTriggered!!) {
                    if (!lastNotificationTriggers.contains(t)) {
                        activatedTriggers.add(t)
                    }
                }
            }
            if (activatedTriggers.isNotEmpty() && frequencyCheck(frequency, lastNotificationTime)) {
                handleNotifications(
                    activatedTriggers,
                    lastNotificationTriggers,
                    mContext,
                    lastNotificationTime
                )
            }
        } else {
            lastNotificationTime = 5.0
        }
    }


}