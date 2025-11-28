package com.pikosplash.hydroballs.keopge.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.pikosplash.hydroballs.keopge.presentation.app.PikoSplashApplication

class PikoSplashPushHandler {
    fun pikoSplashHandlePush(extras: Bundle?) {
        Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = pikoSplashBundleToMap(extras)
            Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    PikoSplashApplication.PIKO_SPLASH_FB_LI = map["url"]
                    Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Push data no!")
        }
    }

    private fun pikoSplashBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}