package com.simpleapps.weather.ui.search.result

import androidx.databinding.ObservableField
import com.simpleapps.weather.core.BaseViewModel
import com.simpleapps.weather.db.entity.CitiesForSearchEntity
import javax.inject.Inject

/**
 * Created by Furkan on 2019-11-04
 */

class SearchResultItemViewModel @Inject internal constructor() : BaseViewModel() {
    var item = ObservableField<CitiesForSearchEntity>()
}
