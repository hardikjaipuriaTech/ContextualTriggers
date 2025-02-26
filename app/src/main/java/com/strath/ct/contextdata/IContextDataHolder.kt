package com.strath.ct.contextdata

import com.strath.ct.contextdatasources.WeatherResult

interface IContextDataHolder {

    val weatherForecast: WeatherResult?
    val batteryLevel: Int
    val todayEvents: List<Any?>?
    val endOfDayTime: Long

    fun getSteps(date: Long): Int
}