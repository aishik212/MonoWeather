package com.simpleapps.weather.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.simpleapps.weather.domain.model.Wind
import kotlinx.android.parcel.Parcelize

/**
 * Created by Furkan on 2019-10-24
 */

@Parcelize
@Entity(tableName = "Wind")
data class WindEntity(
        @ColumnInfo(name = "deg")
        val deg: Double?,
        @ColumnInfo(name = "speed")
        val speed: Double?
) : Parcelable {
    @Ignore
    constructor(wind: Wind?) : this(
            deg = wind?.deg,
            speed = wind?.speed
    )
}
