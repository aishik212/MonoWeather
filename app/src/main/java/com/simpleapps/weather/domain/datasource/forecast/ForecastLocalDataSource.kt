package com.simpleapps.weather.domain.datasource.forecast

import com.simpleapps.weather.db.dao.ForecastDao
import com.simpleapps.weather.db.entity.ForecastEntity
import com.simpleapps.weather.domain.model.ForecastResponse
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-21
 */

class ForecastLocalDataSource @Inject constructor(private val forecastDao: ForecastDao) {

    fun getForecast() = forecastDao.getForecast()

    fun insertForecast(forecast: ForecastResponse) = forecastDao.deleteAndInsert(ForecastEntity(forecast))
}
