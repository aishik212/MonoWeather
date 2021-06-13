package com.simpleapps.weather.ui.dashboard

import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.simpleapps.weather.R
import com.simpleapps.weather.core.BaseFragment
import com.simpleapps.weather.core.Constants
import com.simpleapps.weather.databinding.FragmentDashboardBinding
import com.simpleapps.weather.di.Injectable
import com.simpleapps.weather.domain.model.ListItem
import com.simpleapps.weather.domain.usecase.CurrentWeatherUseCase
import com.simpleapps.weather.domain.usecase.ForecastUseCase
import com.simpleapps.weather.ui.dashboard.forecast.ForecastAdapter
import com.simpleapps.weather.ui.main.MainActivity
import com.simpleapps.weather.utils.extensions.isNetworkAvailable
import com.simpleapps.weather.utils.extensions.observeWith

class DashboardFragment : BaseFragment<DashboardFragmentViewModel, FragmentDashboardBinding>(
    R.layout.fragment_dashboard,
    DashboardFragmentViewModel::class.java
), Injectable {
    var action: DashboardFragmentDirections.ActionDashboardFragmentToWeatherDetailFragment? = null

    override fun init() {
        super.init()
        initForecastAdapter()
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        val lat: String? = binding.viewModel?.sharedPreferences?.getString(Constants.Coords.LAT, "")
        val lon: String? = binding.viewModel?.sharedPreferences?.getString(Constants.Coords.LON, "")

        if (lat?.isNotEmpty() == true && lon?.isNotEmpty() == true) {
            binding.viewModel?.setCurrentWeatherParams(
                CurrentWeatherUseCase.CurrentWeatherParams(
                    lat,
                    lon,
                    isNetworkAvailable(requireContext()),
                    Constants.Coords.METRIC
                )
            )
            binding.viewModel?.setForecastParams(
                ForecastUseCase.ForecastParams(
                    lat,
                    lon,
                    isNetworkAvailable(requireContext()),
                    Constants.Coords.METRIC
                )
            )
        }
        binding.viewModel?.getCurrentWeatherViewState()?.observeWith(
            viewLifecycleOwner
        ) {
            with(binding) {
                containerForecast.viewState = it
                val cardView = containerForecast.cardView
                cardView.setOnClickListener {
                    weatherClick(it.rootView)
                }
                Log.d("texts", "init: " + cardView)
            }
        }
        binding.viewModel?.getForecastViewState()?.observeWith(
            viewLifecycleOwner
        ) {
            with(binding) {
                viewState = it
                it.data?.list?.let { forecasts ->
                    action =
                        DashboardFragmentDirections.actionDashboardFragmentToWeatherDetailFragment(
                            forecasts[0]
                        )
                    initForecast(forecasts.subList(1, forecasts.size))
                }
                (activity as MainActivity).viewModel.toolbarTitle.set(it.data?.city?.getCityAndCountry())
            }
        }

    }

    fun weatherClick(v: View) {
        Log.d("texts", "weatherClick: CLICKED")
        Log.d("texts", "weatherClick: " + action)
        if ("$action" != "null") {
            findNavController()
                .navigate(
                    action!!,
                    FragmentNavigator.Extras.Builder()
                        .addSharedElements(
                            mapOf(
                                v.findViewById<CardView>(R.id.cardView) to v.findViewById<CardView>(
                                    R.id.cardView
                                ).transitionName,
                                v.findViewById<ImageView>(R.id.imageViewWeatherIcon) to v.findViewById<ImageView>(
                                    R.id.imageViewWeatherIcon
                                ).transitionName,
                                v.findViewById<TextView>(R.id.textViewTemperature) to v.findViewById<TextView>(
                                    R.id.textViewTemperature
                                ).transitionName,
                                v.findViewById<TextView>(R.id.textViewWeatherMain) to v.findViewById<TextView>(
                                    R.id.textViewWeatherMain
                                ).transitionName,
                                v.findViewById<TextView>(R.id.textViewHumidityHeader) to v.findViewById<TextView>(
                                    R.id.textViewHumidityHeader
                                ).transitionName,
                                v.findViewById<TextView>(R.id.textViewHumidity) to v.findViewById<TextView>(
                                    R.id.textViewHumidity
                                ).transitionName,
                            )
                        )
                        .build()
                )

        }
    }

    private fun initForecastAdapter() {
        val adapter = ForecastAdapter { item, cardView, forecastIcon, dayOfWeek, temp, tempMaxMin ->
            val action =
                DashboardFragmentDirections.actionDashboardFragmentToWeatherDetailFragment(item)
            findNavController()
                .navigate(
                    action,
                    FragmentNavigator.Extras.Builder()
                        .addSharedElements(
                            mapOf(
                                cardView to cardView.transitionName,
                                forecastIcon to forecastIcon.transitionName,
                                dayOfWeek to dayOfWeek.transitionName,
                                temp to temp.transitionName,
                                tempMaxMin to tempMaxMin.transitionName
                            )
                        )
                        .build()
                )
        }

        binding.recyclerForecast.adapter = adapter
        binding.recyclerForecast.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        postponeEnterTransition()
        binding.recyclerForecast.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
    }

    private fun initForecast(list: List<ListItem>) {
        (binding.recyclerForecast.adapter as ForecastAdapter).submitList(list)
    }
}
