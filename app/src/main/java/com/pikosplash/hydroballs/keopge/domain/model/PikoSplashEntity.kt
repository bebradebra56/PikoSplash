package com.pikosplash.hydroballs.keopge.domain.model

import com.google.gson.annotations.SerializedName


data class PikoSplashEntity (
    @SerializedName("ok")
    val pikoSplashOk: String,
    @SerializedName("url")
    val pikoSplashUrl: String,
    @SerializedName("expires")
    val pikoSplashExpires: Long,
)