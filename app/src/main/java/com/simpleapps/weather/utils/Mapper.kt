package com.simpleapps.weather.utils

/**
 * Created by Furkan on 2019-10-26
 */

interface Mapper<R, D> {
    fun mapFrom(type: R): D
}
