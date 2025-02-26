package com.strath.ct.trigger

import android.content.Intent
import com.strath.ct.contextdata.IContextDataHolder

/**
 * A composite trigger is one that combines multiple SimpleTrigger objects into a single
 * trigger. Information from each SimpleTrigger is used to determine whether or not a single
 * prompt should be sent.
 *
 */
open class CompositeTrigger(private val mTriggers: List<ITrigger>, private val holder: IContextDataHolder) : ITrigger {
    private val mHolder: IContextDataHolder = holder

    override val complexity: Int
        get() {
        var complexity = 0
        for (trigger in mTriggers) {
            complexity += trigger.complexity
        }
        return complexity
    }

    override val notificationTitle: String? = null

    override val notificationMessage: String? = null

    override val notificationIntent: Intent? = null

    override val isTriggered: Boolean? = null
}