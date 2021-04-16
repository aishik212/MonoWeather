package com.simpleapps.weather.core

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.simpleapps.weather.R
import com.simpleapps.weather.utils.extensions.hide
import com.simpleapps.weather.utils.extensions.show
import com.squareup.picasso.Picasso

/**
 * Created by Furkan on 2019-10-16
 */

@BindingAdapter("app:visibility")
fun setVisibilty(view: View, isVisible: Boolean) {
    if (isVisible) {
        view.show()
    } else {
        view.hide()
    }
}

@BindingAdapter("app:setWeatherIcon")
fun setWeatherIcon(view: ImageView, iconPath: String?) {
    if (iconPath.isNullOrEmpty())
        return
    Picasso.get().cancelRequest(view)
    val newPath = iconPath.replace(iconPath, "a$iconPath")
    try {
        val imageid = view.context.resources.getIdentifier(newPath + "_svg", "drawable", view.context.packageName)
        val imageDrawable = ResourcesCompat.getDrawable(view.resources, imageid, view.context.theme)
        view.setImageDrawable(imageDrawable)
    } catch (e: Exception) {
        Log.d("texts", "setWeatherIcon: c $iconPath  $newPath " + e.localizedMessage)
    }
}

@BindingAdapter("app:setErrorView")
fun setErrorView(view: View, viewState: BaseViewState?) {
    if (viewState?.shouldShowErrorMessage() == true)
        view.show()
    else
        view.hide()

    view.setOnClickListener { view.hide() }
}

@BindingAdapter("app:setErrorText")
fun setErrorText(view: TextView, viewState: BaseViewState?) {
    if (viewState?.shouldShowErrorMessage() == true)
        view.text = viewState.getErrorMessage()
    else
        view.text = view.context.getString(R.string.unexpected_exception)
}
