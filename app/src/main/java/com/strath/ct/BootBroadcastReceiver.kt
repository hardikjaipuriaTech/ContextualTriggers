package com.strath.ct


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * The BootBroadcastReceiver  is a special type of BroadcastReceiver that
 * receives a broadcast message when the device completes its boot process.
 *
 */
class BootBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CT BootBroadcastReceiver", "Calling Intent MainActivity ")
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(mainActivityIntent)
    }
}