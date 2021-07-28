package com.simpleapps.weather.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity

class CacheUtils {
    companion object {
        enum class CACHEVAL {
            WEATHER
        }

        fun setCache(activity: FragmentActivity?, response: String?, key: CACHEVAL) {
            if (activity != null && response != null && response.trim() != "") {
                initCache(activity).edit().putString(key.toString(), response).apply();
            }
        }

        fun getCache(activity: FragmentActivity?, key: CACHEVAL): String? {
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