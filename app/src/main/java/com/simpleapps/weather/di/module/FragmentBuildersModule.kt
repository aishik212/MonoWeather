package com.simpleapps.weather.di.module

import com.simpleapps.weather.ui.dashboard.DashboardFragment
import com.simpleapps.weather.ui.search.SearchFragment
import com.simpleapps.weather.ui.splash.SplashFragment
import com.simpleapps.weather.ui.weather_detail.WeatherDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Furkan on 2019-10-26
 */

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeDashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeWeatherDetailFragment(): WeatherDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}
