package com.simpleapps.weather.core

/**
 * Created by Furkan on 2019-10-16
 */

object Constants {

    object NetworkService {
        const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
        const val API_KEY_VALUE = "52d5837024fcbaf664779366d4a7bc84"
        const val RATE_LIMITER_TYPE = "data"
        const val API_KEY_QUERY = "appid"
    }

    object AlgoliaKeys {
        const val APPLICATION_ID = "plNW8IW0YOIN"
        const val SEARCH_API_KEY = "029766644cb160efa51f2a32284310eb"
    }

    object Coords {
        const val LAT = "lat"
        const val LON = "lon"
        const val METRIC = "metric"
    }
}
