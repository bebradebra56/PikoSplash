package com.pikosplash.hydroballs.keopge.domain.model

import com.google.gson.annotations.SerializedName


private const val PIKO_SPLASH_A = "com.pikosplash.hydroballs"
private const val PIKO_SPLASH_B = "chickranch"
data class PikoSplashParam (
    @SerializedName("af_id")
    val pikoSplashAfId: String,
    @SerializedName("bundle_id")
    val pikoSplashBundleId: String = PIKO_SPLASH_A,
    @SerializedName("os")
    val pikoSplashOs: String = "Android",
    @SerializedName("store_id")
    val pikoSplashStoreId: String = PIKO_SPLASH_A,
    @SerializedName("locale")
    val pikoSplashLocale: String,
    @SerializedName("push_token")
    val pikoSplashPushToken: String,
    @SerializedName("firebase_project_id")
    val pikoSplashFirebaseProjectId: String = PIKO_SPLASH_B,

    )