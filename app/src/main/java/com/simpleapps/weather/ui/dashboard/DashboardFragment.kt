package com.simpleapps.weather.ui.dashboard

import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.simpleapps.cacheutils.CacheUtils
import com.simpleapps.weather.R
import com.simpleapps.weather.core.BaseFragment
import com.simpleapps.weather.core.Constants.NetworkService.API_KEY_VALUE
import com.simpleapps.weather.databinding.FragmentDashboardBinding
import com.simpleapps.weather.di.Injectable
import com.simpleapps.weather.domain.model.CurrentWeatherResponse
import com.simpleapps.weather.domain.model.ListItem
import com.simpleapps.weather.ui.dashboard.forecast.ForecastAdapter
import com.simpleapps.weather.ui.main.MainActivity
import com.simpleapps.weather.utils.typeconverters.DataConverter
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
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
        /*       sharedElementReturnTransition =
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
       *//*            binding.viewModel?.setForecastParams(
                ForecastUseCase.ForecastParams(
                    lat,
                    lon,
                    isNetworkAvailable(requireContext()),
                    Constants.Coords.METRIC
                )
            )*//*
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
        }*/
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
        loadCurrentWeather()
        loadForecast()
        var city = com.simpleapps.cacheutils.CacheUtils.getCache(
            activity,
            com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.CITY
        )
        if (city == null) {
            city = fetchCityFromCoord(city)
        }
        (activity as MainActivity).viewModel.toolbarTitle.set(city)
    }

    private fun loadCurrentWeather() {
        val key = com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.CURRENT_WEATHER
        val cache = com.simpleapps.cacheutils.CacheUtils.getCache(
            activity,
            key
        )
        val timeLeft = com.simpleapps.cacheutils.CacheUtils.getCacheTime(
            activity,
            key
        )
        if (cache != null && timeLeft > 0) {
            //          LOAD FROM CACHE and Check TimeLeft
            loadFromCache(cache, key)
        } else {
            loadFromApi(key)
        }
    }

    private fun loadForecast() {
        try {
            val key = com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.WEATHER_FORECAST
            val cache = com.simpleapps.cacheutils.CacheUtils.getCache(
                activity,
                key
            )
            val timeLeft = com.simpleapps.cacheutils.CacheUtils.getCacheTime(
                activity,
                key
            )
            if (cache != null && timeLeft > 0) {
                //          LOAD FROM CACHE and Check TimeLeft
                loadFromCache(cache, key)
            } else {
                loadFromApi(key)
            }
        } catch (e: Exception) {
            Log.d("texts", "fetchLocation: " + e.localizedMessage)
        }
    }

    private fun loadFromCache(
        cache: String,
        cacheval: com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL
    ) {
        if (cacheval == com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.WEATHER_FORECAST) {
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
                loadFromApi(com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.WEATHER_FORECAST)
            }
        } else if (cacheval == com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.CURRENT_WEATHER) {
            setCurrentWeatherUI(cache)
        }
    }

    private fun loadFromApi(cacheval: com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL) {
        var city = com.simpleapps.cacheutils.CacheUtils.getCache(
            activity,
            com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.CITY
        )
        if (city == null) {
            city = fetchCityFromCoord(city)
        }
        val queue = Volley.newRequestQueue(requireContext())
        if (cacheval == com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.WEATHER_FORECAST) {
            val url = "http://api.openweathermap.org/data/2.5/forecast?q=${
                city?.replace(
                    "\n",
                    ""
                )
            }&appid=${API_KEY_VALUE}&units=metric"
            val stringRequest = StringRequest(Request.Method.GET, url,
                { response ->
                    com.simpleapps.cacheutils.CacheUtils.setCache(
                        activity, response,
                        cacheval
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
            queue.add(stringRequest)
        } else if (cacheval == com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.CURRENT_WEATHER) {
            val url = "http://api.openweathermap.org/data/2.5/weather?q=${
                city?.replace(
                    "\n",
                    ""
                )
            }&appid=${API_KEY_VALUE}&units=metric"
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    com.simpleapps.cacheutils.CacheUtils.setCache(
                        activity, response,
                        cacheval
                    )
                    setCurrentWeatherUI(response)
                },
                {
                    Log.d("texts", "init: " + it.localizedMessage)
                }
            )
            queue.add(stringRequest)
        }
    }

    private fun setCurrentWeatherUI(response: String) {
        val fromJson = Gson().fromJson<CurrentWeatherResponse>(
            response,
            CurrentWeatherResponse::class.java
        )
        val main = fromJson.main
        val weather = fromJson.weather?.first()
        val containerForecast = binding.containerForecast
        containerForecast.textViewWeatherMain.text =
            weather?.main
        containerForecast.textViewTemperature.text =
            getTempString(main?.temp.toString())
        setWeatherIcon(containerForecast.imageViewWeatherIcon, weather?.icon)
        containerForecast.textViewHumidity.text =
            """${main?.humidity.toString()}%"""
        containerForecast.cardView.setCardBackgroundColor(getColor(fromJson.dt?.toLong()))
    }

    fun getTempString(temp: String): String {
        return temp.toString().substringBefore(".") + "°"
    }

    fun setWeatherIcon(view: ImageView, iconPath: String?) {
        if (iconPath.isNullOrEmpty())
            return
        Picasso.get().cancelRequest(view)
        val newPath = iconPath.replace(iconPath, "a$iconPath")
        try {
            val imageid = view.context.resources.getIdentifier(
                newPath + "_svg",
                "drawable",
                view.context.packageName
            )
            val imageDrawable =
                ResourcesCompat.getDrawable(view.resources, imageid, view.context.theme)
            view.setImageDrawable(imageDrawable)
        } catch (e: Exception) {
        }
    }


    fun getColor(dt: Long?): Int {
        return when (dt?.let { getDateTime(it) }) {
            DayOfWeek.MONDAY -> Color.parseColor("#E57373")
            DayOfWeek.TUESDAY -> Color.parseColor("#BA68C8")
            DayOfWeek.WEDNESDAY -> Color.parseColor("#7986CB")
            DayOfWeek.THURSDAY -> Color.parseColor("#4FC3F7")
            DayOfWeek.FRIDAY -> Color.parseColor("#4DB6AC")
            DayOfWeek.SATURDAY -> Color.parseColor("#81C784")
            DayOfWeek.SUNDAY -> Color.parseColor("#DCE775")
            else -> Color.parseColor("#E57373")
        }
    }

    private fun getDateTime(s: Long): DayOfWeek? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val netDate = Date(s * 1000)
            val formattedDate = sdf.format(netDate)

            LocalDate.of(
                formattedDate.substringAfterLast("/").toInt(),
                formattedDate.substringAfter("/").take(2).toInt(),
                formattedDate.substringBefore("/").toInt()
            )
                .dayOfWeek
        } catch (e: Exception) {
            e.printStackTrace()
            DayOfWeek.MONDAY
        }
    }

    private fun fetchCityFromCoord(city: String?): String? {
        var city1 = city
        val latitude = com.simpleapps.cacheutils.CacheUtils.getCache(
            activity,
            com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.lat
        )
        val longitude = com.simpleapps.cacheutils.CacheUtils.getCache(
            activity,
            com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.lon
        )
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
        val adapter =
            ForecastAdapter { item, cardView, forecastIcon, dayOfWeek, temp, tempMaxMin ->
                val action =
                    DashboardFragmentDirections.actionDashboardFragmentToWeatherDetailFragment(
                        item
                    )
                Log.d("texts", "initForecastAdapter: ")
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

    var fval: ListItem? = null
    private fun initForecast(list: List<ListItem>) {
        fval = list.first()
        (binding.recyclerForecast.adapter as ForecastAdapter).submitList(list)
    }
}
