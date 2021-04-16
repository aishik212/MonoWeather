package com.simpleapps.weather.domain

import com.simpleapps.weather.domain.model.CurrentWeatherResponse
import com.simpleapps.weather.domain.model.ForecastResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Furkan on 2019-10-16
 */

interface WeatherAppAPI {

    @GET("forecast")
    fun getForecastByGeoCords(
            @Query("lat")
            lat: Double,
            @Query("lon")
            lon: Double,
            @Query("units")
            units: String
    ): Single<ForecastResponse>

    @GET("weather")
    fun getCurrentByGeoCords(
            @Query("lat")
            lat: Double,
            @Query("lon")
            lon: Double,
            @Query("units")
            units: String
    ): Single<CurrentWeatherResponse>
}
