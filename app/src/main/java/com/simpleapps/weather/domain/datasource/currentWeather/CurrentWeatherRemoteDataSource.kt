package com.simpleapps.weather.domain.datasource.currentWeather

import com.simpleapps.weather.domain.WeatherAppAPI
import com.simpleapps.weather.domain.model.CurrentWeatherResponse
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-24
 */

class CurrentWeatherRemoteDataSource @Inject constructor(private val api: WeatherAppAPI) {

    fun getCurrentWeatherByGeoCords(lat: Double, lon: Double, units: String): Single<CurrentWeatherResponse> = api.getCurrentByGeoCords(lat, lon, units)
}
