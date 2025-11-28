package com.pikosplash.hydroballs.keopge.data.shar

import android.content.Context
import androidx.core.content.edit

class PikoSplashSharedPreference(context: Context) {
    private val pikoSplashPrefs = context.getSharedPreferences("pikoSplashSharedPrefsAb", Context.MODE_PRIVATE)

    var pikoSplashSavedUrl: String
        get() = pikoSplashPrefs.getString(PIKO_SPLASH_SAVED_URL, "") ?: ""
        set(value) = pikoSplashPrefs.edit { putString(PIKO_SPLASH_SAVED_URL, value) }

    var pikoSplashExpired : Long
        get() = pikoSplashPrefs.getLong(PIKO_SPLASH_EXPIRED, 0L)
        set(value) = pikoSplashPrefs.edit { putLong(PIKO_SPLASH_EXPIRED, value) }

    var pikoSplashAppState: Int
        get() = pikoSplashPrefs.getInt(PIKO_SPLASH_APPLICATION_STATE, 0)
        set(value) = pikoSplashPrefs.edit { putInt(PIKO_SPLASH_APPLICATION_STATE, value) }

    var pikoSplashNotificationRequest: Long
        get() = pikoSplashPrefs.getLong(PIKO_SPLASH_NOTIFICAITON_REQUEST, 0L)
        set(value) = pikoSplashPrefs.edit { putLong(PIKO_SPLASH_NOTIFICAITON_REQUEST, value) }

    var pikoSplashNotificationRequestedBefore: Boolean
        get() = pikoSplashPrefs.getBoolean(PIKO_SPLASH_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = pikoSplashPrefs.edit { putBoolean(
            PIKO_SPLASH_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val PIKO_SPLASH_SAVED_URL = "pikoSplashSavedUrl"
        private const val PIKO_SPLASH_EXPIRED = "pikoSplashExpired"
        private const val PIKO_SPLASH_APPLICATION_STATE = "pikoSplashApplicationState"
        private const val PIKO_SPLASH_NOTIFICAITON_REQUEST = "pikoSplashNotificationRequest"
        private const val PIKO_SPLASH_NOTIFICATION_REQUEST_BEFORE = "pikoSplashNotificationRequestedBefore"
    }
}