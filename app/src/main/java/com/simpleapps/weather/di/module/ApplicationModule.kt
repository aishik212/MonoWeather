package com.simpleapps.weather.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences = application.getSharedPreferences(application.packageName + "_preferences", Context.MODE_PRIVATE)
}
