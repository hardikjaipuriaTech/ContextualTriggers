package com.strath.ct.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.strath.ct.R
import com.strath.ct.trigger.ITrigger
import java.text.SimpleDateFormat
import java.util.*

fun handleNotifications(
    activatedTriggers: List<ITrigger>,
    lastNotificationTriggers: MutableList<ITrigger>,
    mContext: Context,
    lastNotificationTime: Double
) {
    var bestTrigger = activatedTriggers[0]
    for (t in activatedTriggers) {
        if (t.complexity > bestTrigger.complexity) {
            bestTrigger = t
        }
    }
    if (lastNotificationTriggers.size == 3) {
        lastNotificationTriggers.removeAt(0)
    }

    lastNotificationTriggers.add(bestTrigger)
    sendNotification(
        1, bestTrigger.notificationTitle, bestTrigger.notificationMessage,
        bestTrigger.notificationIntent, mContext, lastNotificationTime
    )
}

fun sendNotification(
    id: Int,
    title: String?,
    message: String?,
    intent: Intent?,
    mContext: Context,
    lastNotificationTime: Double
) {
    val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(mContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    if (intent != null) {
        mBuilder.setContentIntent(PendingIntent.getActivity(mContext, 0, intent, 0))
    }
    val mNotifyMgr: NotificationManager =
        mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    // Builds the notification and issues it.
    Log.d("TriggerManager", "Notification Sent")
    mNotifyMgr.createNotificationChannel(
        NotificationChannel(
            "CHANNEL_ID",
            "CHANNEL_NAME",
            NotificationManager.IMPORTANCE_DEFAULT
        )
    )
    mNotifyMgr.notify(id, mBuilder.build())

}

fun frequencyCheck(frequency: Double, lastNotificationTime: Double): Boolean {
    val timeDifference = time - lastNotificationTime
    return timeDifference >= frequency
}

val time: Double
    get() {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("HH.mm")
        return dateFormat.format(cal.time).toDouble()
    }