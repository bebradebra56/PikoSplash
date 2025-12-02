package com.pikosplash.hydroballs.keopge.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pikosplash.hydroballs.PikoSplashActivity
import com.pikosplash.hydroballs.R
import com.pikosplash.hydroballs.keopge.presentation.app.PikoSplashApplication

private const val PIKO_SPLASH_CHANNEL_ID = "piko_splash_notifications"
private const val PIKO_SPLASH_CHANNEL_NAME = "PikoSplash Notifications"
private const val PIKO_SPLASH_NOT_TAG = "PikoSplash"

class PikoSplashPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                pikoSplashShowNotification(it.title ?: PIKO_SPLASH_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                pikoSplashShowNotification(it.title ?: PIKO_SPLASH_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            pikoSplashHandleDataPayload(remoteMessage.data)
        }
    }

    private fun pikoSplashShowNotification(title: String, message: String, data: String?) {
        val pikoSplashNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PIKO_SPLASH_CHANNEL_ID,
                PIKO_SPLASH_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            pikoSplashNotificationManager.createNotificationChannel(channel)
        }

        val pikoSplashIntent = Intent(this, PikoSplashActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pikoSplashPendingIntent = PendingIntent.getActivity(
            this,
            0,
            pikoSplashIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pikoSplashNotification = NotificationCompat.Builder(this, PIKO_SPLASH_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.piko_splash_noti_icon)
            .setAutoCancel(true)
            .setContentIntent(pikoSplashPendingIntent)
            .build()

        pikoSplashNotificationManager.notify(System.currentTimeMillis().toInt(), pikoSplashNotification)
    }

    private fun pikoSplashHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}