package com.strath.ct.contextdatasources

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Parcel
import android.provider.CalendarContract
import android.util.Pair
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

private const val TAG = "CalendarWorker"
// Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
private val EVENT_PROJECTION: Array<String> = arrayOf(
    CalendarContract.Instances.TITLE, // 0
    CalendarContract.Instances.EVENT_LOCATION, // 1
    CalendarContract.Instances.BEGIN // 2
)

// The indices for the projection array above.
const val PROJECTION_TITLE_INDEX: Int = 0
const val PROJECTION_EVENT_LOCATION_INDEX: Int = 1
const val PROJECTION_BEGIN_INDEX: Int = 2

class CalendarWorker (context: Context, params: WorkerParameters) : Worker(context, params) {

    private val contentResolver: ContentResolver = context.contentResolver

    override fun doWork(): Result {
        val period = todayMillis
        var results: ArrayList<CalendarEvent?>? = null
        val builder: Uri.Builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, period.first)
        ContentUris.appendId(builder, period.second)
        val cur: Cursor? = contentResolver.query(builder.build(), EVENT_PROJECTION, null, null, null)
        if (cur != null) {
            results = ArrayList()
            while (cur.moveToNext()) {
                // Get the field values
                val title: String = cur.getString(PROJECTION_TITLE_INDEX)
                val location: String = cur.getString(PROJECTION_EVENT_LOCATION_INDEX)
                val beginVal: Long = cur.getLong(PROJECTION_BEGIN_INDEX)

                val parcel: Parcel = Parcel.obtain()
                parcel.writeString(title)
                parcel.writeString(location)
                parcel.writeLong(beginVal)
                parcel.setDataPosition(0)
                val event = CalendarEvent.CREATOR.createFromParcel(parcel)
                results.add(event)
                parcel.recycle()
            }
            cur.close()
        }
        return Result.success()
    }

    /**
     * Creates a pair of milliseconds from now to to end of the day.
     *
     * @return Pair from now to 23:59:59.999 tonight.
     */
    private val todayMillis: Pair<Long, Long>
        private get() {
            val now = System.currentTimeMillis()
            val today = Calendar.getInstance()
            today[Calendar.HOUR_OF_DAY] = 23
            today[Calendar.MINUTE] = 59
            today[Calendar.SECOND] = 59
            today[Calendar.MILLISECOND] = 999
            return Pair(now, today.timeInMillis)
        }
}