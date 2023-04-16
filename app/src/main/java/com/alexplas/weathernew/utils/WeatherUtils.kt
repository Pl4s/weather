package com.alexplas.weathernew.utils

import android.content.Context
import android.widget.ImageView
import com.alexplas.weathernew.R
import com.alexplas.weathernew.utils.Constant.BASE_WEATHER_ICON_URL
import com.alexplas.weathernew.utils.Constant.IMAGE_SUFFIX
import com.bumptech.glide.Glide
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.unixTimestampToLocalTimeString(timezoneOffset: Int): String {
    return try {
        val instant = Instant.ofEpochSecond(this)
        val zoneOffset = ZoneOffset.ofTotalSeconds(timezoneOffset)
        val localDateTime = LocalDateTime.ofInstant(instant, zoneOffset)
        val outputDateFormat = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
        localDateTime.format(outputDateFormat)
    } catch (e: Exception) {
        e.printStackTrace()
        this.toString()
    }
}

fun Long.unixTimestampToDateString(): String {
    try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this*1000L

        val outputDateFormat = SimpleDateFormat("dd/MMM", Locale.ENGLISH)
        outputDateFormat.timeZone = TimeZone.getDefault() // user's default time zone
        return outputDateFormat.format(calendar.time)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return this.toString()
}

fun String.dateStringToUnixTimestamp(): Long {
    try {
        val inputDateFormat = SimpleDateFormat("dd/MMM", Locale.ENGLISH)
        inputDateFormat.timeZone = TimeZone.getDefault()

        val date = inputDateFormat.parse(this)
        if (date != null) {
            return date.time / 1000L
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return 0L
}

fun Long.unixTimestampToTimeString() : String {

    try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this*1000L

        val outputDateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        outputDateFormat.timeZone = TimeZone.getDefault()
        return outputDateFormat.format(calendar.time)

    } catch (e: Exception) {
        e.printStackTrace()
    }

    return this.toString()
}

fun String.timeStringToUnixTimestamp(): Long {
    try {
        val inputTimeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        inputTimeFormat.timeZone = TimeZone.getDefault()

        val time = inputTimeFormat.parse(this)
        if (time != null) {
            return time.time / 1000L
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return 0L
}

fun loadWeatherIcon(context: Context, weatherIcon: String, imageView: ImageView) {
    val iconURL = "${BASE_WEATHER_ICON_URL}${weatherIcon}${IMAGE_SUFFIX}"
    Glide.with(context)
        .load(iconURL)
        .into(imageView)
}

fun roundTo4DecimalPlaces(value: Double): Double {
    return BigDecimal(value).setScale(4, RoundingMode.HALF_UP).toDouble()
}

fun getBackgroundResource(weatherId: Int): Int {
    return when (weatherId) {
        in 200..232 -> R.drawable.thunderstorm_bg
        in 300..321 -> R.drawable.drizzle_bg
        in 500..531 -> R.drawable.rain_bg
        in 600..622 -> R.drawable.snow_bg
        in 701..781 -> R.drawable.atmosphere_bg
        800 -> R.drawable.clear_bg
        in 801..804 -> R.drawable.clouds_bg
        else -> R.drawable.clear_bg
    }
}






