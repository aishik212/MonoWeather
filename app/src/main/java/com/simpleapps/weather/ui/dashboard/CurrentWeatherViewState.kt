package com.simpleapps.weather.ui.dashboard

import com.simpleapps.weather.core.BaseViewState
import com.simpleapps.weather.db.entity.CurrentWeatherEntity
import com.simpleapps.weather.utils.domain.Status

/**
 * Created by Furkan on 2019-10-24
 */

class CurrentWeatherViewState(
        val status: Status,
        val error: String? = null,
        val data: CurrentWeatherEntity? = null
) : BaseViewState(status, error) {
    fun getForecast() = data
}
