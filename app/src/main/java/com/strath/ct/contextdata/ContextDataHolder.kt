package com.strath.ct.contextdata

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.Toast
import com.strath.ct.contextdatasources.CalendarEvent
import com.strath.ct.contextdatasources.WeatherResult
import com.strath.ct.db.DBHelper
import java.util.*

class ContextDataHolder(private val mContext: Context) : IContextDataHolder {

    private var calendarEvents: List<CalendarEvent>? = null

    override var todayEvents: List<Any>?
        get() = calendarEvents
        set(events) {
            calendarEvents = events as List<CalendarEvent>?
        }

    override var weatherForecast: WeatherResult? = null

    override val batteryLevel: Int
        get() {
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = mContext.registerReceiver(null, ifilter)
            return if (batteryStatus != null) {
                val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = level / scale.toFloat() * 100
                println("BATTERY LEVEL:$batteryPct")
                batteryPct.toInt()
            } else {
                -1
            }
        }


    override val endOfDayTime: Long
        get() = DBHelper.avgWorkExitTime

    override fun getSteps(date: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MILLISECOND] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return DBHelper.getSteps(cal.timeInMillis)
    }

    fun addSteps(steps: Int) {
        var steps = steps
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        steps += DBHelper.getSteps(cal.timeInMillis)
        DBHelper.addSteps(cal.timeInMillis, steps)
        Toast.makeText(mContext, "Steps walked today: $steps", Toast.LENGTH_SHORT).show()
    }
}
