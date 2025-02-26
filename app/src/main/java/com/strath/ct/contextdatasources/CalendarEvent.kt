package com.strath.ct.contextdatasources

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * A basic object for storing information on a calendar event in the user's calendar.
 *
 */
class CalendarEvent private constructor(`in`: Parcel) : Parcelable {

    val title: String?  = `in`.readString()

    val location: String? = `in`.readString()

    val startTime: Long = `in`.readLong()

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(title)
        parcel.writeString(location)
        parcel.writeLong(startTime)
    }

    companion object {
        const val TAG = "todayEvents"
        @JvmField val CREATOR: Creator<CalendarEvent> = object : Creator<CalendarEvent> {
            override fun createFromParcel(`in`: Parcel): CalendarEvent? {
                return CalendarEvent(`in`)
            }

            override fun newArray(size: Int): Array<CalendarEvent?> {
                return arrayOfNulls(size)
            }
        }
    }
}