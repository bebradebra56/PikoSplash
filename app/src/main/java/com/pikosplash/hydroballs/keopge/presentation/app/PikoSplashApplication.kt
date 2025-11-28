package com.pikosplash.hydroballs.keopge.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.pikosplash.hydroballs.keopge.presentation.di.pikoSplashModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface PikoSplashAppsFlyerState {
    data object PikoSplashDefault : PikoSplashAppsFlyerState
    data class PikoSplashSuccess(val pikoSplashData: MutableMap<String, Any>?) :
        PikoSplashAppsFlyerState

    data object PikoSplashError : PikoSplashAppsFlyerState
}

interface PikoSplashAppsApi {
    @Headers("Content-Type: application/json")
    @GET(PIKO_SPLASH_LIN)
    fun pikoSplashGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val PIKO_SPLASH_APP_DEV = "ZpZxFozBa8WWttHidEHQ27"
private const val PIKO_SPLASH_LIN = "com.pikosplash.hydroballs"

class PikoSplashApplication : Application() {
    private var pikoSplashIsResumed = false
    private var pikoSplashConversionTimeoutJob: Job? = null
    private var pikoSplashDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        pikoSplashSetDebufLogger(appsflyer)
        pikoSplashMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        pikoSplashExtractDeepMap(p0.deepLink)
                        Log.d(PIKO_SPLASH_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(PIKO_SPLASH_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(PIKO_SPLASH_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            PIKO_SPLASH_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    pikoSplashConversionTimeoutJob?.cancel()
                    Log.d(PIKO_SPLASH_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = pikoSplashGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.pikoSplashGetClient(
                                    devkey = PIKO_SPLASH_APP_DEV,
                                    deviceId = pikoSplashGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(PIKO_SPLASH_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    pikoSplashResume(PikoSplashAppsFlyerState.PikoSplashError)
                                } else {
                                    pikoSplashResume(
                                        PikoSplashAppsFlyerState.PikoSplashSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(PIKO_SPLASH_MAIN_TAG, "Error: ${d.message}")
                                pikoSplashResume(PikoSplashAppsFlyerState.PikoSplashError)
                            }
                        }
                    } else {
                        pikoSplashResume(PikoSplashAppsFlyerState.PikoSplashSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    pikoSplashConversionTimeoutJob?.cancel()
                    Log.d(PIKO_SPLASH_MAIN_TAG, "onConversionDataFail: $p0")
                    pikoSplashResume(PikoSplashAppsFlyerState.PikoSplashError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(PIKO_SPLASH_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(PIKO_SPLASH_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, PIKO_SPLASH_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(PIKO_SPLASH_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(PIKO_SPLASH_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        pikoSplashStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@PikoSplashApplication)
            modules(
                listOf(
                    pikoSplashModule
                )
            )
        }
    }

    private fun pikoSplashExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(PIKO_SPLASH_MAIN_TAG, "Extracted DeepLink data: $map")
        pikoSplashDeepLinkData = map
    }

    private fun pikoSplashStartConversionTimeout() {
        pikoSplashConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!pikoSplashIsResumed) {
                Log.d(PIKO_SPLASH_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                pikoSplashResume(PikoSplashAppsFlyerState.PikoSplashError)
            }
        }
    }

    private fun pikoSplashResume(state: PikoSplashAppsFlyerState) {
        pikoSplashConversionTimeoutJob?.cancel()
        if (state is PikoSplashAppsFlyerState.PikoSplashSuccess) {
            val convData = state.pikoSplashData ?: mutableMapOf()
            val deepData = pikoSplashDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!pikoSplashIsResumed) {
                pikoSplashIsResumed = true
                pikoSplashConversionFlow.value = PikoSplashAppsFlyerState.PikoSplashSuccess(merged)
            }
        } else {
            if (!pikoSplashIsResumed) {
                pikoSplashIsResumed = true
                pikoSplashConversionFlow.value = state
            }
        }
    }

    private fun pikoSplashGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(PIKO_SPLASH_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun pikoSplashSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun pikoSplashMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun pikoSplashGetApi(url: String, client: OkHttpClient?): PikoSplashAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var pikoSplashInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val pikoSplashConversionFlow: MutableStateFlow<PikoSplashAppsFlyerState> = MutableStateFlow(
            PikoSplashAppsFlyerState.PikoSplashDefault
        )
        var PIKO_SPLASH_FB_LI: String? = null
        const val PIKO_SPLASH_MAIN_TAG = "PikoSplashMainTag"
    }
}