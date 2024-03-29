package com.simpleapps.weather.db.entity

import android.os.Parcelable
import androidx.room.*
import com.simpleapps.weather.domain.model.ForecastResponse
import com.simpleapps.weather.domain.model.ListItem
import kotlinx.android.parcel.Parcelize

/**
 * Created by Furkan on 2019-10-21
 */

@Parcelize
@Entity(tableName = "Forecast")
data class ForecastEntity(

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int,

        @Embedded
        var city: CityEntity?,

        @ColumnInfo(name = "list")
        var list: List<ListItem>?
) : Parcelable {

    @Ignore
    constructor(forecastResponse: ForecastResponse) : this(
            id = 0,
            city = forecastResponse.city?.let { CityEntity(it) },
            list = forecastResponse.list
    )
}
