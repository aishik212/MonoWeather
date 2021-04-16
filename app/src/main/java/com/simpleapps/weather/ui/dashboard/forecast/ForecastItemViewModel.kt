package com.simpleapps.weather.ui.dashboard.forecast

import androidx.databinding.ObservableField
import com.simpleapps.weather.core.BaseViewModel
import com.simpleapps.weather.domain.model.ListItem
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-25
 */

class ForecastItemViewModel @Inject internal constructor() : BaseViewModel() {
    var item = ObservableField<ListItem>()
}
