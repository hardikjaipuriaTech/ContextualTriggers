package com.strath.ct.trigger.implementation

import android.content.Intent
import com.strath.ct.contextdata.IContextDataHolder
import com.strath.ct.trigger.SimpleTrigger

class BatteryTrigger(holder: IContextDataHolder) : SimpleTrigger(holder) {

    private val mContextHolder: IContextDataHolder
    private val mTitle: String
    private val mMessage: String
    private val mThreshold: Int

    init {
        mContextHolder = holder
        mThreshold = 50
        mTitle = "Battery Trigger"
        mMessage = "There is enough battery on your mobile to go for a Walk"
    }

    override val notificationTitle: String?= mTitle

    override val notificationMessage: String? = mMessage

    override val notificationIntent: Intent? = null

    override val isTriggered: Boolean?
        get() {
        val batteryLevel: Int = mContextHolder.batteryLevel
        return batteryLevel >= mThreshold && batteryLevel != -1
    }
}