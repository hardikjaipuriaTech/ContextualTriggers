package com.strath.ct.contextdatasources

/**
 * The WeatherForecastType is an abstraction of results obtained from any external weather data source.
 * Any implementation of a weather data source should convert the external source's classification
 * into these values.
 *
 */
enum class WeatherForecastType
    (
    private val mDescription: String
) {
    CLEAR("clear"),
    CLOUDY("cloudy"),
    DRIZZLE("drizzle"),
    RAIN("rain"),
    SNOW("snow"),
    THUNDERSTORM("thunderstorm");
}