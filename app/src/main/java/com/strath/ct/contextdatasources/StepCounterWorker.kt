package com.strath.ct.contextdatasources

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.strath.ct.ContextDataManager

/**
 * The number of steps to be walked for the counter to send an intent.
 */
private const val THRESHOLD = 50

class StepCounterWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams),
    SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val mContext: Context = context

    private var previousY: Float = 0.0f
    private var stepCount: Int = 0

    /**
     * The total number of steps walked since booting.
     */
    private var mTotalStepsSinceBoot = 0
    private var mFirstLoad = true
    private var magnitudePrevious = 0.0

    override fun doWork(): Result {
        var intent: Intent? = null
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            val sensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
            Log.d(TAG, "successfully registered")
            return Result.success()
        } else {
            Log.e(TAG, "Accelerometer sensor not found")

            intent = Intent(mContext, ContextDataManager::class.java)
            intent.putExtra("DataSource", "Steps")
            intent.putExtra("Count", -1)
            Log.d(TAG, "Intent ContextDataManager Service sent from StepCounterWorker")
            mContext.startService(intent)
            return Result.failure()
        }

    }

    override fun onSensorChanged(event: SensorEvent) {
        /*val currentY = event.values[1]
        if (previousY > 0 && currentY < 0) {
            Log.d(TAG, "Old step count : "+ stepCount)
            stepCount++
            Log.d(TAG, "New step count : "+ stepCount)
        }
        previousY = currentY*/

        if (event != null) {
            var steps: Int = 0;
            if (mFirstLoad) {
                val xacceleration: Float = event.values[0]
                val yacceleration: Float = event.values[1]
                val zacceleration: Float = event.values[2]
                val magnitude =
                    Math.sqrt((xacceleration * xacceleration + yacceleration * yacceleration + zacceleration * zacceleration).toDouble())
                val magnitudeDelta: Double = magnitude - magnitudePrevious
                magnitudePrevious = magnitude
                if (event.values.get(0) > 6) {
                    steps++
                }
                mTotalStepsSinceBoot = steps
                mFirstLoad = false
            } else {
                val diff = steps - mTotalStepsSinceBoot
                if (diff > THRESHOLD) {
                    mTotalStepsSinceBoot = steps
                    val intent = Intent(mContext, ContextDataManager::class.java)
                    intent.putExtra("DataSource", "Steps")
                    intent.putExtra("Count", diff)
                    Log.d(TAG, "Intent ContextDataManager Service sent from StepCounterWorker")
                    mContext.startService(intent)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onStopped() {
        super.onStopped()
        sensorManager.unregisterListener(this)
        Log.d(TAG, "Step counter stopped")
    }

    companion object {
        private const val TAG = "StepCounterWorker"
    }

}