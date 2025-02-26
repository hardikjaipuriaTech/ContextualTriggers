package com.strath.ct.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@get:Throws(ParseException::class)
val currentTime: Date
    get() {
        val currentCal = Calendar.getInstance()
        val currentTimeStr = SimpleDateFormat("HH:mm:ss").format(currentCal.time)
        val currentTime = SimpleDateFormat("HH:mm:ss").parse(currentTimeStr)
        currentCal.time = currentTime
        currentCal.add(Calendar.DATE, 1)
        return currentCal.time
    }


