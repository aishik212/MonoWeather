package com.simpleapps.weather.ui.dashboard

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.simpleapps.weather.core.BaseViewModel
import com.simpleapps.weather.domain.usecase.CurrentWeatherUseCase
import com.simpleapps.weather.domain.usecase.ForecastUseCase
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-24
 */

class DashboardFragmentViewModel @Inject internal constructor(private val forecastUseCase: ForecastUseCase, private val currentWeatherUseCase: CurrentWeatherUseCase, var sharedPreferences: SharedPreferences) : BaseViewModel() {

    private val _forecastParams: MutableLiveData<ForecastUseCase.ForecastParams> = MutableLiveData()
    private val _currentWeatherParams: MutableLiveData<CurrentWeatherUseCase.CurrentWeatherParams> = MutableLiveData()

    fun getForecastViewState() = forecastViewState
    fun getCurrentWeatherViewState() = currentWeatherViewState

    private val forecastViewState: LiveData<ForecastViewState> = _forecastParams.switchMap { params ->
        forecastUseCase.execute(params)
    }
    private val currentWeatherViewState: LiveData<CurrentWeatherViewState> = _currentWeatherParams.switchMap { params ->
        currentWeatherUseCase.execute(params)
    }

    fun setForecastParams(params: ForecastUseCase.ForecastParams) {
        if (_forecastParams.value == params)
            return
        _forecastParams.postValue(params)
    }

    fun setCurrentWeatherParams(params: CurrentWeatherUseCase.CurrentWeatherParams) {
        if (_currentWeatherParams.value == params)
            return
        _currentWeatherParams.postValue(params)
    }
}
