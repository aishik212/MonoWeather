package com.simpleapps.weather.ui.dashboard

import android.location.Address
import android.location.Geocoder
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.simpleapps.weather.R
import com.simpleapps.weather.core.BaseFragment
import com.simpleapps.weather.core.Constants
import com.simpleapps.weather.core.Constants.NetworkService.API_KEY_VALUE
import com.simpleapps.weather.databinding.FragmentDashboardBinding
import com.simpleapps.weather.di.Injectable
import com.simpleapps.weather.domain.model.ListItem
import com.simpleapps.weather.domain.usecase.CurrentWeatherUseCase
import com.simpleapps.weather.ui.dashboard.forecast.ForecastAdapter
import com.simpleapps.weather.utils.CacheUtils
import com.simpleapps.weather.utils.extensions.isNetworkAvailable
import com.simpleapps.weather.utils.extensions.observeWith
import com.simpleapps.weather.utils.typeconverters.DataConverter
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : BaseFragment<DashboardFragmentViewModel, FragmentDashboardBinding>(
    R.layout.fragment_dashboard,
    DashboardFragmentViewModel::class.java
), Injectable {
    private var action: DashboardFragmentDirections.ActionDashboardFragmentToWeatherDetailFragment? =
        null
    private var dtList: MutableList<String> = mutableListOf()
    private val arr: ArrayList<ListItem> = arrayListOf()
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
/*            binding.viewModel?.setForecastParams(
                ForecastUseCase.ForecastParams(
                    lat,
                    lon,
                    isNetworkAvailable(requireContext()),
                    Constants.Coords.METRIC
                )
            )*/
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
            }
        }
        /*
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
                }
                (activity as MainActivity).viewModel.toolbarTitle.set(it.data?.city?.getCityAndCountry())
            }
        }*/
        try {
            val cache = CacheUtils.getCache(
                activity,
                CacheUtils.Companion.CACHEVAL.WEATHER
            )
            val timeLeft = CacheUtils.getCacheTime(
                activity,
                CacheUtils.Companion.CACHEVAL.WEATHER
            )
            if (cache != null && timeLeft > 0) {
//          LOAD FROM CACHE and Check TimeLeft
                loadFromCache(cache)
            } else {
                loadFromApi()
            }

        } catch (e: Exception) {
            Log.d("texts", "fetchLocation: " + e.localizedMessage)
        }

    }

    private fun loadFromCache(cache: String): Any? {
        val jsonArray = JSONArray(JSONObject(cache).get("list").toString())
        val stringToList = DataConverter.stringToList(jsonArray.toString())
        return if (stringToList != null) {
            stringToList.iterator().forEach {
                val element = it.dtTxt.toString().split(" ")[0]
                if (!dtList.contains(element)) {
                    dtList.add(element)
                    arr.add(it)
                }
            }
            val alist: List<ListItem> = arr
            initForecast(alist)
        } else {
            loadFromApi()
        }
    }

    private fun loadFromApi(): Request<String>? {
        var city = CacheUtils.getCache(activity, CacheUtils.Companion.CACHEVAL.CITY)
        if (city == null) {
            city = fetchCityFromCoord(city)
        }
        Log.d("texts", "loadFromApi: " + city)
        val url = "http://api.openweathermap.org/data/2.5/forecast?q=${
            city?.replace(
                "\n",
                ""
            )
        }&appid=${API_KEY_VALUE}&units=metric"
        val queue = Volley.newRequestQueue(requireContext())
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                CacheUtils.setCache(
                    activity, response,
                    CacheUtils.Companion.CACHEVAL.WEATHER
                )
                val jsonArray =
                    JSONArray(JSONObject(response).get("list").toString())
                val stringToList =
                    DataConverter.stringToList(jsonArray.toString())
                if (stringToList != null) {
                    stringToList.iterator().forEach {
                        val element = it.dtTxt.toString().split(" ")[0]
                        if (!dtList.contains(element)) {
                            dtList.add(element)
                            arr.add(it)
                        }
                    }
                    val alist: List<ListItem> = arr
                    initForecast(alist)
                }

            },
            {
                Log.d("texts", "init: " + it.localizedMessage)
            }
        )
        return queue.add(stringRequest)
    }

    private fun fetchCityFromCoord(city: String?): String? {
        var city1 = city
        val latitude = CacheUtils.getCache(activity, CacheUtils.Companion.CACHEVAL.lat)
        val longitude = CacheUtils.getCache(activity, CacheUtils.Companion.CACHEVAL.lon)
        val latitude1 = latitude
        val longitude1 = longitude
        Log.d("texts", """fetchLocation: $latitude1 $longitude1 """)
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        if (latitude1 != null && longitude1 != null) {
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude1.toDouble(), longitude1.toDouble(), 1)
            city1 = addresses[0].locality
        }
        return city1
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }

    fun weatherClick(v: View) {
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
