package com.simpleapps.weather.domain.model

import android.graphics.Color
import android.os.Parcelable
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

    fun getColor(): Int {
        return when (dtTxt?.let { getDateTime(it) }) {
            DayOfWeek.MONDAY -> Color.parseColor("#E57373")
            DayOfWeek.TUESDAY -> Color.parseColor("#BA68C8")
            DayOfWeek.WEDNESDAY -> Color.parseColor("#7986CB")
            DayOfWeek.THURSDAY -> Color.parseColor("#4FC3F7")
            DayOfWeek.FRIDAY -> Color.parseColor("#4DB6AC")
            DayOfWeek.SATURDAY -> Color.parseColor("#81C784")
            DayOfWeek.SUNDAY -> Color.parseColor("#DCE775")
            else -> Color.parseColor("#E57373")
        }
    }

    fun getHourColor(): Int {
        return when (dtTxt?.substringAfter(" ")?.substringBeforeLast(":")) {
            "00:00" -> Color.parseColor("#E57373")
            "03:00" -> Color.parseColor("#BA68C8")
            "06:00" -> Color.parseColor("#7986CB")
            "09:00" -> Color.parseColor("#4FC3F7")
            "12:00" -> Color.parseColor("#4DB6AC")
            "15:00" -> Color.parseColor("#81C784")
            "18:00" -> Color.parseColor("#DCE775")
            "21:00" -> Color.parseColor("#FFB74D")
            else -> Color.parseColor("#E57373")
        }
    }

    fun getHourOfDay(): String {
        return dtTxt?.substringAfter(" ")?.substringBeforeLast(":") ?: "00:00"
    }
}
