package com.simpleapps.weather.di.module

import android.content.Context
import androidx.room.Room
import com.simpleapps.weather.db.WeatherDatabase
import com.simpleapps.weather.db.dao.CitiesForSearchDao
import com.simpleapps.weather.db.dao.CurrentWeatherDao
import com.simpleapps.weather.db.dao.ForecastDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun getDatabase(context: Context): WeatherDatabase {
        return Room.databaseBuilder(
                context,
                WeatherDatabase::class.java, "WeatherApp-DB"
        ).build()
    }

    @Singleton
    @Provides
    fun provideForecastDao(db: WeatherDatabase): ForecastDao {
        return db.forecastDao()
    }

    @Singleton
    @Provides
    fun provideCurrentWeatherDao(db: WeatherDatabase): CurrentWeatherDao {
        return db.currentWeatherDao()
    }

    @Singleton
    @Provides
    fun provideCitiesForSearchDao(db: WeatherDatabase): CitiesForSearchDao {
        return db.citiesForSearchDao()
    }
}
