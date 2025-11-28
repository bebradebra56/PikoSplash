package com.pikosplash.hydroballs.keopge.domain.usecases

import android.util.Log
import com.pikosplash.hydroballs.keopge.data.repo.PikoSplashRepository
import com.pikosplash.hydroballs.keopge.data.utils.PikoSplashPushToken
import com.pikosplash.hydroballs.keopge.data.utils.PikoSplashSystemService
import com.pikosplash.hydroballs.keopge.domain.model.PikoSplashEntity
import com.pikosplash.hydroballs.keopge.domain.model.PikoSplashParam
import com.pikosplash.hydroballs.keopge.presentation.app.PikoSplashApplication

class PikoSplashGetAllUseCase(
    private val pikoSplashRepository: PikoSplashRepository,
    private val pikoSplashSystemService: PikoSplashSystemService,
    private val pikoSplashPushToken: PikoSplashPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : PikoSplashEntity?{
        val params = PikoSplashParam(
            pikoSplashLocale = pikoSplashSystemService.pikoSplashGetLocale(),
            pikoSplashPushToken = pikoSplashPushToken.pikoSplashGetToken(),
            pikoSplashAfId = pikoSplashSystemService.pikoSplashGetAppsflyerId()
        )
        Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Params for request: $params")
        return pikoSplashRepository.pikoSplashGetClient(params, conversion)
    }



}