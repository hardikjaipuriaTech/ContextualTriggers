package com.strath.ct.trigger

import android.content.Intent

/**
 * The basic trigger interface. The purpose of a trigger is to prompt the user to perform some
 * behaviour. A trigger will determine the best time to notify the user based on some contextual
 * information it has access to.
 *
 */
interface ITrigger {
    val complexity: Int
    val notificationTitle: String?
    val notificationMessage: String?
    val notificationIntent: Intent?
    val isTriggered: Boolean?
}