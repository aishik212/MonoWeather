package com.simpleapps.weather.ui.weather_detail

import android.transition.TransitionInflater
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.simpleapps.cacheutils.CacheUtils
import com.simpleapps.weather.R
import com.simpleapps.weather.core.BaseFragment
import com.simpleapps.weather.databinding.FragmentWeatherDetailBinding
import com.simpleapps.weather.di.Injectable
import com.simpleapps.weather.domain.model.ListItem
import com.simpleapps.weather.ui.weather_detail.weatherHourOfDay.WeatherHourOfDayAdapter
import com.simpleapps.weather.utils.extensions.observeWith
import com.simpleapps.weather.utils.typeconverters.DataConverter
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONArray
import org.json.JSONObject

class WeatherDetailFragment : BaseFragment<WeatherDetailViewModel, FragmentWeatherDetailBinding>(R.layout.fragment_weather_detail, WeatherDetailViewModel::class.java), Injectable {

    private val weatherDetailFragmentArgs: WeatherDetailFragmentArgs by navArgs()
    var disposable = CompositeDisposable()

    override fun init() {
        super.init()

        binding.viewModel?.weatherItem?.set(weatherDetailFragmentArgs.weatherItem)
        binding.viewModel?.selectedDayDate =
            weatherDetailFragmentArgs.weatherItem.dtTxt?.substringBefore(" ")
        binding.viewModel?.getForecast()?.observeWith(viewLifecycleOwner) {
            binding.viewModel?.selectedDayForecastLiveData
                ?.postValue(
                    it.list?.filter { item ->
                        item.dtTxt?.substringBefore(" ") == binding.viewModel?.selectedDayDate
                    }
                )
        }

        val key = com.simpleapps.cacheutils.CacheUtils.Companion.CACHEVAL.WEATHER_FORECAST
        val cache = com.simpleapps.cacheutils.CacheUtils.getCache(
            activity,
            key
        )
        val jsonArray = JSONArray(JSONObject(cache).get("list").toString())
        val stringToList = DataConverter.stringToList(jsonArray.toString())
        val filter = stringToList?.filter { item ->
            item.dtTxt?.substringBefore(" ") == binding.viewModel?.selectedDayDate
        }
        if (filter != null) {
            initWeatherHourOfDayAdapter(filter)
        }


        binding.fabClose.setOnClickListener {
            findNavController().popBackStack()
        }

        val inflateTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = inflateTransition
//        initWeatherHourOfDayAdapter()
    }

    private fun initWeatherHourOfDayAdapter(list: List<ListItem>) {
        val adapter = WeatherHourOfDayAdapter { _ ->
            // TODO - onClick
        }

        binding.recyclerViewHourOfDay.adapter = adapter
        (binding.recyclerViewHourOfDay.adapter as WeatherHourOfDayAdapter).submitList(list)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
