package com.simpleapps.weather.db.entity

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import androidx.room.*
import com.simpleapps.weather.domain.model.CurrentWeatherResponse
import com.simpleapps.weather.domain.model.WeatherItem
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Furkan on 2019-10-24
 */

@Parcelize
@Entity(tableName = "CurrentWeather")
data class CurrentWeatherEntity(
        @ColumnInfo(name = "visibility")
        var visibility: Int?,
        @ColumnInfo(name = "timezone")
        var timezone: Int?,
        @Embedded
        var main: MainEntity?,
        @Embedded
        var clouds: CloudsEntity?,
        @ColumnInfo(name = "dt")
        var dt: Long?,
        @ColumnInfo(name = "weather")
        val weather: List<WeatherItem?>? = null,
        @ColumnInfo(name = "name")
        val name: String?,
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Int,
        @ColumnInfo(name = "base")
        val base: String?,
        @Embedded
        val wind: WindEntity?
) : Parcelable {
    @Ignore
    constructor(currentWeather: CurrentWeatherResponse) : this(
            visibility = currentWeather.visibility,
            timezone = currentWeather.timezone,
            main = MainEntity(currentWeather.main),
            clouds = CloudsEntity(currentWeather.clouds),
            dt = currentWeather.dt?.toLong(),
            weather = currentWeather.weather,
            name = currentWeather.name,
            id = 0,
            base = currentWeather.base,
            wind = WindEntity(currentWeather.wind)
    )

    fun getCurrentWeather(): WeatherItem? {
        return weather?.first()
    }

    private fun getDateTime(s: Long): DayOfWeek? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val netDate = Date(s * 1000)
            val formattedDate = sdf.format(netDate)

            LocalDate.of(
                    formattedDate.substringAfterLast("/").toInt(),
                    formattedDate.substringAfter("/").take(2).toInt(),
                    formattedDate.substringBefore("/").toInt()
            )
                    .dayOfWeek
        } catch (e: Exception) {
            e.printStackTrace()
            DayOfWeek.MONDAY
        }
    }

    fun getColor(dt: Long?,context: Context): Int {
        return when (dt?.let { getDateTime(it) }) {
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
}
