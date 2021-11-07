package com.simpleapps.weather.domain.model

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.simpleapps.weather.R
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class ListItem(

    @Json(name = "dt")
    val dt: Long?,

    @Json(name = "rain")
    val rain: Rain?,

    @Json(name = "dt_txt")
    val dtTxt: String?,

    @Json(name = "snow")
    val snow: Snow?,

    @Json(name = "weather")
    val weather: List<WeatherItem?>?,

    @Json(name = "main")
    val main: Main?,

    @Json(name = "clouds")
    val clouds: Clouds?,

    @Json(name = "sys")
    val sys: Sys?,

    @Json(name = "wind")
    val wind: Wind?
) : Parcelable {
    fun getWeatherItem(): WeatherItem? {
        return weather?.first()
    }

    fun getDay(): String? {
        return dtTxt?.let { getDateTime(it)?.getDisplayName(TextStyle.FULL, Locale.getDefault()) }
    }

    private fun getDateTime(s: String): DayOfWeek? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
//            val netDate = Date(s * 1000)
//            val formattedDate = sdf.format(netDate)
            val ss = s.substringBefore(" ")
            val dayOfWeek = LocalDate.of(
                ss.substringBefore("-").toInt(),
                ss.substringAfter("-").take(2).toInt(),
                ss.substringAfterLast("-").toInt()
            ).dayOfWeek
            dayOfWeek
        } catch (e: Exception) {
            DayOfWeek.MONDAY
        }
    }

    fun getColor(context: Context): Int {
        return when (dtTxt?.let { getDateTime(it) }) {
            DayOfWeek.MONDAY -> ContextCompat.getColor(context, R.color.colorLevelA)
            DayOfWeek.TUESDAY -> ContextCompat.getColor(context, R.color.colorLevelB)
            DayOfWeek.WEDNESDAY -> ContextCompat.getColor(context, R.color.colorLevelC)
            DayOfWeek.THURSDAY -> ContextCompat.getColor(context, R.color.colorLevelD)
            DayOfWeek.FRIDAY -> ContextCompat.getColor(context, R.color.colorLevelE)
            DayOfWeek.SATURDAY -> ContextCompat.getColor(context, R.color.colorLevelF)
            DayOfWeek.SUNDAY -> ContextCompat.getColor(context, R.color.colorLevelG)
            else -> ContextCompat.getColor(context, R.color.colorLevelA)
        }
    }

    fun getHourColor(context: Context): Int {
        return when (dtTxt?.substringAfter(" ")?.substringBeforeLast(":")) {
            "00:00" -> ContextCompat.getColor(context, R.color.colorLevelA)
            "03:00" -> ContextCompat.getColor(context, R.color.colorLevelB)
            "06:00" -> ContextCompat.getColor(context, R.color.colorLevelC)
            "09:00" -> ContextCompat.getColor(context, R.color.colorLevelD)
            "12:00" -> ContextCompat.getColor(context, R.color.colorLevelE)
            "15:00" -> ContextCompat.getColor(context, R.color.colorLevelF)
            "18:00" -> ContextCompat.getColor(context, R.color.colorLevelG)
            "21:00" -> ContextCompat.getColor(context, R.color.colorLevelH)
            else -> ContextCompat.getColor(context, R.color.colorLevelA)
        }
    }

    fun getHourOfDay(): String {
        return dtTxt?.substringAfter(" ")?.substringBeforeLast(":") ?: "00:00"
    }
}
