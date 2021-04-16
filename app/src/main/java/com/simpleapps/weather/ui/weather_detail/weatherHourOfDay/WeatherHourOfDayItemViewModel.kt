package com.simpleapps.weather.ui.weather_detail.weatherHourOfDay

import androidx.databinding.ObservableField
import com.simpleapps.weather.core.BaseViewModel
import com.simpleapps.weather.domain.model.ListItem
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-26
 */

class WeatherHourOfDayItemViewModel @Inject internal constructor() : BaseViewModel() {
    var item = ObservableField<ListItem>()
}
