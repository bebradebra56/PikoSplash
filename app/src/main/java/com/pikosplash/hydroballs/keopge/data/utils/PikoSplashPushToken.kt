package com.pikosplash.hydroballs.keopge.data.utils

import android.util.Log
import com.pikosplash.hydroballs.keopge.presentation.app.PikoSplashApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlin.Exception

class PikoSplashPushToken {

    suspend fun pikoSplashGetToken(
        pikoSplashMaxAttempts: Int = 3,
        pikoSplashDelayMs: Long = 1500
    ): String {

        repeat(pikoSplashMaxAttempts - 1) {
            try {
                val pikoSplashToken = FirebaseMessaging.getInstance().token.await()
                return pikoSplashToken
            } catch (e: Exception) {
                Log.e(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(pikoSplashDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}