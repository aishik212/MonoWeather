package com.simpleapps.cacheutils

import android.content.Context
import android.content.SharedPreferences

class CacheUtils {
    companion object {
        enum class CACHEVAL {
            WEATHER_FORECAST, CITY, lat, lon, CURRENT_WEATHER, OPEN_COUNT
        }

        fun setCache(activity: Context?, response: String?, key: CACHEVAL) {
            if (activity != null && response != null && response.trim() != "") {
                initCache(activity).edit().putString(key.toString(), response).apply();
                var l = (System.currentTimeMillis() + (1000L * 6L * 60L * 60L))
                when (key) {
                    CACHEVAL.WEATHER_FORECAST -> {
                        l = (System.currentTimeMillis() + (1000L * 6L * 60L * 60L))
                    }
                    CACHEVAL.CURRENT_WEATHER -> {
                        l = (System.currentTimeMillis() + (1000L * 1L * 60L * 60L) / 12)
                    }
                    else -> {
                        l = (System.currentTimeMillis() + (1000L * 6L * 60L * 60L))
                    }
                }

                initCache(activity).edit().putLong(key.toString() + "_time", l).apply();
            }
        }

        fun getCacheTime(activity: Context?, key: CACHEVAL): Long {
            if (activity != null) {
                return initCache(activity).getLong(
                    key.toString() + "_time",
                    0
                ) - System.currentTimeMillis()
            }
            return 0
        }

        fun getCache(activity: Context?, key: CACHEVAL): String? {
            if (activity != null) {
                return initCache(activity).getString(key.toString(), null)
            }
            return null
        }

        fun initCache(c: Context): SharedPreferences {
            spref = c.getSharedPreferences(c.packageName + "_preferences", Context.MODE_PRIVATE)
            return spref
        }


        lateinit var spref: SharedPreferences
    }
}