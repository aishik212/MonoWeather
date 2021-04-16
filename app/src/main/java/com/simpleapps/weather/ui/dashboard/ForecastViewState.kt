package com.simpleapps.weather.ui.dashboard

import com.simpleapps.weather.core.BaseViewState
import com.simpleapps.weather.db.entity.ForecastEntity
import com.simpleapps.weather.utils.domain.Status

/**
 * Created by Furkan on 2019-10-23
 */

class ForecastViewState(
        val status: Status,
        val error: String? = null,
        val data: ForecastEntity? = null
) : BaseViewState(status, error) {
    fun getForecast() = data
}
