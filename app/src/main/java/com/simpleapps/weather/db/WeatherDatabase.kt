package com.simpleapps.weather.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.simpleapps.weather.db.dao.CitiesForSearchDao
import com.simpleapps.weather.db.dao.CurrentWeatherDao
import com.simpleapps.weather.db.dao.ForecastDao
import com.simpleapps.weather.db.entity.CitiesForSearchEntity
import com.simpleapps.weather.db.entity.CurrentWeatherEntity
import com.simpleapps.weather.db.entity.ForecastEntity
import com.simpleapps.weather.utils.typeconverters.DataConverter

@Database(
        entities = [
            ForecastEntity::class,
            CurrentWeatherEntity::class,
            CitiesForSearchEntity::class
        ],
        version = 2
)
@TypeConverters(DataConverter::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun forecastDao(): ForecastDao

    abstract fun currentWeatherDao(): CurrentWeatherDao

    abstract fun citiesForSearchDao(): CitiesForSearchDao
}
