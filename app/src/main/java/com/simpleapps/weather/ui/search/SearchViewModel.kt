package com.simpleapps.weather.ui.search

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.simpleapps.weather.core.BaseViewModel
import com.simpleapps.weather.core.Constants
import com.simpleapps.weather.db.entity.CoordEntity
import com.simpleapps.weather.domain.usecase.SearchCitiesUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-31
 */

class SearchViewModel @Inject internal constructor(private val useCase: SearchCitiesUseCase, private val pref: SharedPreferences) : BaseViewModel() {

    private val _searchParams: MutableLiveData<SearchCitiesUseCase.SearchCitiesParams> = MutableLiveData()
    fun getSearchViewState() = searchViewState

    private val searchViewState: LiveData<SearchViewState> = _searchParams.switchMap { params ->
        useCase.execute(params)
    }

    fun setSearchParams(params: SearchCitiesUseCase.SearchCitiesParams) {
        if (_searchParams.value == params)
            return
        _searchParams.postValue(params)
    }

    fun saveCoordsToSharedPref(coordEntity: CoordEntity): Single<String>? {
        return Single.create<String> {
            saveLatLong(coordEntity.lat.toString(), coordEntity.lon.toString())
            it.onSuccess("")
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun saveLatLong(lat: String, lon: String) {
        pref.edit().putString(Constants.Coords.LAT, lat).apply()
        pref.edit().putString(Constants.Coords.LON, lon).apply()
    }
}
