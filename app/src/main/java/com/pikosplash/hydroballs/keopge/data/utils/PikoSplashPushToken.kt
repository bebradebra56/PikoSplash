package com.pikosplash.hydroballs.keopge.data.utils

import android.util.Log
import com.pikosplash.hydroballs.keopge.presentation.app.PikoSplashApplication
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PikoSplashPushToken {

    suspend fun pikoSplashGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}