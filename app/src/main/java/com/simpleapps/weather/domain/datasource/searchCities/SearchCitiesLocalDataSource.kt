package com.simpleapps.weather.domain.datasource.searchCities

import androidx.lifecycle.LiveData
import com.simpleapps.weather.db.dao.CitiesForSearchDao
import com.simpleapps.weather.db.entity.CitiesForSearchEntity
import com.simpleapps.weather.domain.model.SearchResponse
import javax.inject.Inject

/**
 * Created by Furkan on 2019-10-31
 */

class SearchCitiesLocalDataSource @Inject constructor(private val citiesForSearchDao: CitiesForSearchDao) {

    fun getCityByName(cityName: String?): LiveData<List<CitiesForSearchEntity>> = citiesForSearchDao.getCityByName(cityName)

    fun insertCities(response: SearchResponse) {
        response.hits
                ?.map { CitiesForSearchEntity(it) }
                ?.let { citiesForSearchDao.insertCities(it) }
    }
}
