package com.simpleapps.weather.ui.main

import androidx.databinding.ObservableField
import com.simpleapps.weather.core.BaseViewModel
import javax.inject.Inject

class MainActivityViewModel @Inject internal constructor() : BaseViewModel() {
    var toolbarTitle: ObservableField<String> = ObservableField()
}
