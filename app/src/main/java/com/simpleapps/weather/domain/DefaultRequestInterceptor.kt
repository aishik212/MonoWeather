package com.simpleapps.weather.domain

import com.simpleapps.weather.core.Constants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Furkan on 2019-10-21
 */

@Singleton
class DefaultRequestInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url
                .newBuilder()
                .addQueryParameter(Constants.NetworkService.API_KEY_QUERY, Constants.NetworkService.API_KEY_VALUE)
                .build()
        val request = chain.request().newBuilder().url(url).build()
        return chain.proceed(request)
    }
}
