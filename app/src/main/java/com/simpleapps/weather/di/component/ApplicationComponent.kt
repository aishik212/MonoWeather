package com.simpleapps.weather.di.component

import android.app.Application
import com.simpleapps.weather.WeatherApp
import com.simpleapps.weather.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidSupportInjectionModule::class,
            ApplicationModule::class,
            NetModule::class,
            DatabaseModule::class,
            ActivityModule::class,
            ViewModelModule::class
        ]
)

interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(weatherApp: WeatherApp)
}
