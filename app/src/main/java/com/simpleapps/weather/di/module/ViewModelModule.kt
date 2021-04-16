package com.simpleapps.weather.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simpleapps.weather.di.ViewModelFactory
import com.simpleapps.weather.di.key.ViewModelKey
import com.simpleapps.weather.ui.dashboard.DashboardFragmentViewModel
import com.simpleapps.weather.ui.dashboard.forecast.ForecastItemViewModel
import com.simpleapps.weather.ui.main.MainActivityViewModel
import com.simpleapps.weather.ui.search.SearchViewModel
import com.simpleapps.weather.ui.search.result.SearchResultItemViewModel
import com.simpleapps.weather.ui.splash.SplashFragmentViewModel
import com.simpleapps.weather.ui.weather_detail.WeatherDetailViewModel
import com.simpleapps.weather.ui.weather_detail.weatherHourOfDay.WeatherHourOfDayItemViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Furkan on 2019-10-16
 */

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @IntoMap
    @Binds
    @ViewModelKey(SplashFragmentViewModel::class)
    abstract fun provideSplashFragmentViewModel(splashFragmentViewModel: SplashFragmentViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun provideMainViewModel(mainActivityViewModel: MainActivityViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(DashboardFragmentViewModel::class)
    abstract fun provideDashboardFragmentViewModel(dashboardFragmentViewModel: DashboardFragmentViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(ForecastItemViewModel::class)
    abstract fun provideForecastItemViewModel(forecastItemViewModel: ForecastItemViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(WeatherDetailViewModel::class)
    abstract fun provideWeatherDetailViewModel(weatherDetailViewModel: WeatherDetailViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(WeatherHourOfDayItemViewModel::class)
    abstract fun provideWeatherHourOfDayItemViewModel(weatherHourOfDayItemViewModel: WeatherHourOfDayItemViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(SearchViewModel::class)
    abstract fun provideSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(SearchResultItemViewModel::class)
    abstract fun provideSearchResultItemViewModel(searchResultItemViewModel: SearchResultItemViewModel): ViewModel
}
