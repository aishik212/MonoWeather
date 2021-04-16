package com.simpleapps.weather.ui.weather_detail

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simpleapps.weather.core.BaseViewModel
import com.simpleapps.weather.db.entity.ForecastEntity
import com.simpleapps.weather.domain.datasource.forecast.ForecastLocalDataSource
import com.simpleapps.weather.domain.model.ListItem
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-26
 */

class WeatherDetailViewModel @Inject constructor(private val forecastLocalDataSource: ForecastLocalDataSource) : BaseViewModel() {

    var weatherItem: ObservableField<ListItem> = ObservableField()
    private var forecastLiveData: LiveData<ForecastEntity> = MutableLiveData()
    var selectedDayDate: String? = null
    var selectedDayForecastLiveData: MutableLiveData<List<ListItem>> = MutableLiveData()

    fun getForecastLiveData() = forecastLiveData

    fun getForecast(): LiveData<ForecastEntity> {
        return forecastLocalDataSource.getForecast()
    }
}
