package com.simpleapps.weather.ui.search

import com.simpleapps.weather.core.BaseViewState
import com.simpleapps.weather.db.entity.CitiesForSearchEntity
import com.simpleapps.weather.utils.domain.Status

/**
 * Created by Furkan on 2019-10-31
 */

class SearchViewState(
        val status: Status,
        val error: String? = null,
        val data: List<CitiesForSearchEntity>? = null
) : BaseViewState(status, error) {
    fun getSearchResult() = data
}
