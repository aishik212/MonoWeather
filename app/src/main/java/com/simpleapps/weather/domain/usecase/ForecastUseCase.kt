package com.simpleapps.weather.domain.usecase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simpleapps.weather.core.Constants
import com.simpleapps.weather.db.entity.ForecastEntity
import com.simpleapps.weather.repo.ForecastRepository
import com.simpleapps.weather.ui.dashboard.ForecastMapper
import com.simpleapps.weather.ui.dashboard.ForecastViewState
import com.simpleapps.weather.utils.UseCaseLiveData
import com.simpleapps.weather.utils.domain.Resource
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-21
 */

class ForecastUseCase @Inject internal constructor(private val repository: ForecastRepository) : UseCaseLiveData<ForecastViewState, ForecastUseCase.ForecastParams, ForecastRepository>() {

    override fun getRepository(): ForecastRepository {
        return repository
    }

    override fun buildUseCaseObservable(params: ForecastParams?): LiveData<ForecastViewState> {
        return repository.loadForecastByCoord(
                params?.lat?.toDouble() ?: 0.0,
                params?.lon?.toDouble() ?: 0.0,
                params?.fetchRequired
                        ?: false,
                units = params?.units ?: Constants.Coords.METRIC
        ).map {
            it.data?.list?.iterator()?.forEach {
                Log.d("texts", "buildUseCaseObservable: " + it)
            }
            onForecastResultReady(it)
        }
    }

    private fun onForecastResultReady(resource: Resource<ForecastEntity>): ForecastViewState {
        val mappedList = resource.data?.list?.let { ForecastMapper().mapFrom(it) }
        resource.data?.list = mappedList
        return ForecastViewState(
                status = resource.status,
                error = resource.message,
                data = resource.data
        )
    }

    class ForecastParams(
            val lat: String = "",
            val lon: String = "",
            val fetchRequired: Boolean,
            val units: String
    ) : Params()
}
