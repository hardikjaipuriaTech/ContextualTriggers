package com.strath.ct.trigger.implementation

import android.content.Intent
import com.strath.ct.contextdata.IContextDataHolder
import com.strath.ct.db.DBHelper
import com.strath.ct.trigger.SimpleTrigger
import java.util.*

private const val TRIGGER_TITLE = "Steps Trigger"
private const val TRIGGER_TEXT = "You seem to be missing out on your daily step count, may be go for a walk?"
private const val TARGET_STEPS = 10000

class StepsTrigger(holder: IContextDataHolder) : SimpleTrigger(holder) {
    private val mContextHolder: IContextDataHolder
    private var mSteps = 0

    init {
        mContextHolder = holder
    }

    override val notificationTitle: String? = TRIGGER_TITLE
    override val notificationMessage: String? = String.format(TRIGGER_TEXT, mSteps.toString())
    override val notificationIntent: Intent? = null

    override val isTriggered: Boolean?
        get() {
            val cal = Calendar.getInstance()
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0
            mSteps = mContextHolder.getSteps(cal.timeInMillis)
            println("Steps walked today: $mSteps")
            val averageSteps = weeklyAverage
            println("Average steps: $averageSteps")
            val estimation = getEstimation(mSteps)
            println("Estimation: $estimation")
            return estimation < TARGET_STEPS
        }

    /**
     * Provides an estimation of how many steps the user will walk today based on the given number
     * of steps walked so far today.
     *
     * @param steps  steps walked so far today
     * @return estimated total number of steps
     */
    private fun getEstimation(steps: Int): Int {
        val millisInDay = (24 * 60 * 60 * 1000).toLong()
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        val startToday = cal.timeInMillis
        val timeToday = System.currentTimeMillis() - startToday
        val percentage = timeToday.toDouble() / millisInDay
        return (steps / percentage).toInt()
    }

    /**
     * Returns the average number of steps walked by the user over the previous week.
     *
     * @return average number of steps.
     */
    private val weeklyAverage: Int
        private get() {
            val cal = Calendar.getInstance()
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0
            val stepsWeek: MutableList<Int> = ArrayList()
            for (i in 0..6) {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val steps: Int = DBHelper.getSteps(cal.timeInMillis)
                if (steps > 0) {
                    println((i + 1).toString() + " days ago: " + steps)
                    stepsWeek.add(steps)
                }
            }
            var averageSteps = 0
            var count = 0
            for (steps in stepsWeek) {
                if (steps > 0) {
                    averageSteps += steps
                    count++
                }
            }
            if (count > 0) {
                averageSteps /= count
            } else {
                averageSteps = 0
            }
            return averageSteps
        }
}