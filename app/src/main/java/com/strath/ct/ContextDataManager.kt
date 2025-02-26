package com.strath.ct

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.os.PowerManager.*
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.strath.ct.contextdata.ContextDataHolder
import com.strath.ct.db.DBHelper
import com.strath.ct.contextdatasources.*
import com.strath.ct.trigger.TriggerManager
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "ContextDataManager"

/**
 * The ContextDataManager is an Android service that is used to manage the data sources
 * and handle any updates that they may send.
 */
class ContextDataManager : Service() {

    private var triggerManager: TriggerManager? = null
    private var contextHolder: ContextDataHolder? = null
    private var workManager: WorkManager? = null
    private var workRequest1: PeriodicWorkRequest? = null
    private var workRequest2: PeriodicWorkRequest? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Reached service ContextDataManager onCreate")

        workManager = WorkManager.getInstance(applicationContext)
        contextHolder = ContextDataHolder(this)
        triggerManager = TriggerManager(this, contextHolder!!)
        DBHelper.init(applicationContext)

        // When the partial wake lock is held, the CPU will stay awake even if the device is in a low-power sleep mode, allowing your app to continue running in the background.
        val powerManager: PowerManager = getSystemService(POWER_SERVICE) as PowerManager
        val wakeLock: WakeLock = powerManager.newWakeLock( PARTIAL_WAKE_LOCK,"ContextDataManager:WakeLockTag")
        wakeLock.acquire()
        //requestPermissions()

        // Initialize WorkRequests
        workRequest1 = PeriodicWorkRequest.Builder(StepCounterWorker::class.java, 1, TimeUnit.HOURS).build()
        workRequest2 = PeriodicWorkRequest.Builder(WeatherWorker::class.java, 1, TimeUnit.HOURS).build()

        launchServicesWithPermissions()

        wakeLock.release()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        workManager?.enqueue(workRequest1!!)
        workManager?.enqueue(workRequest2!!)

        Log.d(TAG, "Reached service ContextDataManager onStart")
        val bundle: Bundle? = if (intent != null) {
            intent.extras
        } else {
            null
        }
        if (bundle != null) {
            if (intent.hasExtra("DataSource")) {
                // saving data received from datasource
                when (intent.getStringExtra("DataSource")) {
                    "Steps" -> {
                        val steps: Int = intent.getIntExtra("Count", 0)
                        if (steps != -1) {
                            contextHolder?.addSteps(steps)
                            println("Updated steps")
                        }
                    }
                    "Weather" -> {
                        val result: WeatherResult? = intent.getParcelableExtra(WeatherResult.TAG)
                        contextHolder?.weatherForecast = result
                    }

                    "Calendar" -> {
                        val events: ArrayList<CalendarEvent>? =
                            intent.getParcelableArrayListExtra<CalendarEvent>(CalendarEvent.TAG)
                        contextHolder?.todayEvents = events
                    }
                }
                    triggerManager?.manageTriggers()
            }
        }
        return START_STICKY
    }

    private fun isInTimeRange(from: Int, to: Int): Boolean {
        Log.d(TAG, "Checking time range $from $to")
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        return (calendar[Calendar.HOUR_OF_DAY] in from..to)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    private fun launchServicesWithPermissions() {
        val read = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CALENDAR
        ) === PackageManager.PERMISSION_GRANTED
        if (read) {

            val calendarWorker = OneTimeWorkRequestBuilder<CalendarWorker>().build()
            WorkManager.getInstance(this).enqueue(calendarWorker)
        }
    }
}