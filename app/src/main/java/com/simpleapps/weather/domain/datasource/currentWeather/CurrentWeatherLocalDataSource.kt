package com.simpleapps.weather.domain.datasource.currentWeather

import com.simpleapps.weather.db.dao.CurrentWeatherDao
import com.simpleapps.weather.db.entity.CurrentWeatherEntity
import com.simpleapps.weather.domain.model.CurrentWeatherResponse
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-24
 */

class CurrentWeatherLocalDataSource @Inject constructor(private val currentWeatherDao: CurrentWeatherDao) {

    fun getCurrentWeather() = currentWeatherDao.getCurrentWeather()

    fun insertCurrentWeather(currentWeather: CurrentWeatherResponse) = currentWeatherDao.deleteAndInsert(CurrentWeatherEntity(currentWeather))
}
