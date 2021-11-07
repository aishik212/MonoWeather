package com.simpleapps.weather.utils

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import org.json.JSONObject

class AdUtils {

    companion object{
        @JvmStatic
        var adResult: JSONObject? = null

        @JvmStatic
        fun showBannerAd(activity: Activity, adId: String, v: FrameLayout) {
            try {
                val adView = AdView(activity)
                adView.adUnitId = adId
                adView.adSize = getAdSize(activity.windowManager, v, activity)
                val adRequest = AdRequest
                    .Builder()
                    .build()
                adView.adListener = object : AdListener() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        Log.d("texts", "onAdFailedToLoad: " + p0.message)
                        v.visibility = View.GONE
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        v.removeAllViews()
                        v.addView(adView)
                        v.visibility = View.VISIBLE
                    }
                }
                adView.loadAd(adRequest)
/*                if (adResult != null && adResult!!.getBoolean("banner")) {
                } else {
                    v.visibility = View.GONE
                }*/
            } catch (e: Exception) {
                Log.d("texts", "showBannerAd: " + e.localizedMessage)
                v.visibility = View.GONE
            }

        }

        private fun getAdSize(
            windowManager: WindowManager,
            ad_view_container: FrameLayout,
            activity: Activity
        ): AdSize {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = ad_view_container.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
        }

    }

}