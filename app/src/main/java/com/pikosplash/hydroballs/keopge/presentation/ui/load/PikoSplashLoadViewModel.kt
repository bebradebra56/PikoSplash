package com.pikosplash.hydroballs.keopge.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pikosplash.hydroballs.keopge.data.shar.PikoSplashSharedPreference
import com.pikosplash.hydroballs.keopge.data.utils.PikoSplashSystemService
import com.pikosplash.hydroballs.keopge.domain.usecases.PikoSplashGetAllUseCase
import com.pikosplash.hydroballs.keopge.presentation.app.PikoSplashAppsFlyerState
import com.pikosplash.hydroballs.keopge.presentation.app.PikoSplashApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PikoSplashLoadViewModel(
    private val pikoSplashGetAllUseCase: PikoSplashGetAllUseCase,
    private val pikoSplashSharedPreference: PikoSplashSharedPreference,
    private val pikoSplashSystemService: PikoSplashSystemService
) : ViewModel() {

    private val _pikoSplashHomeScreenState: MutableStateFlow<PikoSplashHomeScreenState> =
        MutableStateFlow(PikoSplashHomeScreenState.PikoSplashLoading)
    val pikoSplashHomeScreenState = _pikoSplashHomeScreenState.asStateFlow()

    private var pikoSplashGetApps = false


    init {
        viewModelScope.launch {
            when (pikoSplashSharedPreference.pikoSplashAppState) {
                0 -> {
                    if (pikoSplashSystemService.pikoSplashIsOnline()) {
                        PikoSplashApplication.pikoSplashConversionFlow.collect {
                            when(it) {
                                PikoSplashAppsFlyerState.PikoSplashDefault -> {}
                                PikoSplashAppsFlyerState.PikoSplashError -> {
                                    pikoSplashSharedPreference.pikoSplashAppState = 2
                                    _pikoSplashHomeScreenState.value =
                                        PikoSplashHomeScreenState.PikoSplashError
                                    pikoSplashGetApps = true
                                }
                                is PikoSplashAppsFlyerState.PikoSplashSuccess -> {
                                    if (!pikoSplashGetApps) {
                                        pikoSplashGetData(it.pikoSplashData)
                                        pikoSplashGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _pikoSplashHomeScreenState.value =
                            PikoSplashHomeScreenState.PikoSplashNotInternet
                    }
                }
                1 -> {
                    if (pikoSplashSystemService.pikoSplashIsOnline()) {
                        if (PikoSplashApplication.PIKO_SPLASH_FB_LI != null) {
                            _pikoSplashHomeScreenState.value =
                                PikoSplashHomeScreenState.PikoSplashSuccess(
                                    PikoSplashApplication.PIKO_SPLASH_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > pikoSplashSharedPreference.pikoSplashExpired) {
                            Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Current time more then expired, repeat request")
                            PikoSplashApplication.pikoSplashConversionFlow.collect {
                                when(it) {
                                    PikoSplashAppsFlyerState.PikoSplashDefault -> {}
                                    PikoSplashAppsFlyerState.PikoSplashError -> {
                                        _pikoSplashHomeScreenState.value =
                                            PikoSplashHomeScreenState.PikoSplashSuccess(
                                                pikoSplashSharedPreference.pikoSplashSavedUrl
                                            )
                                        pikoSplashGetApps = true
                                    }
                                    is PikoSplashAppsFlyerState.PikoSplashSuccess -> {
                                        if (!pikoSplashGetApps) {
                                            pikoSplashGetData(it.pikoSplashData)
                                            pikoSplashGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Current time less then expired, use saved url")
                            _pikoSplashHomeScreenState.value =
                                PikoSplashHomeScreenState.PikoSplashSuccess(
                                    pikoSplashSharedPreference.pikoSplashSavedUrl
                                )
                        }
                    } else {
                        _pikoSplashHomeScreenState.value =
                            PikoSplashHomeScreenState.PikoSplashNotInternet
                    }
                }
                2 -> {
                    _pikoSplashHomeScreenState.value =
                        PikoSplashHomeScreenState.PikoSplashError
                }
            }
        }
    }


    private suspend fun pikoSplashGetData(conversation: MutableMap<String, Any>?) {
        val pikoSplashData = pikoSplashGetAllUseCase.invoke(conversation)
        if (pikoSplashSharedPreference.pikoSplashAppState == 0) {
            if (pikoSplashData == null) {
                pikoSplashSharedPreference.pikoSplashAppState = 2
                _pikoSplashHomeScreenState.value =
                    PikoSplashHomeScreenState.PikoSplashError
            } else {
                pikoSplashSharedPreference.pikoSplashAppState = 1
                pikoSplashSharedPreference.apply {
                    pikoSplashExpired = pikoSplashData.pikoSplashExpires
                    pikoSplashSavedUrl = pikoSplashData.pikoSplashUrl
                }
                _pikoSplashHomeScreenState.value =
                    PikoSplashHomeScreenState.PikoSplashSuccess(pikoSplashData.pikoSplashUrl)
            }
        } else  {
            if (pikoSplashData == null) {
                _pikoSplashHomeScreenState.value =
                    PikoSplashHomeScreenState.PikoSplashSuccess(pikoSplashSharedPreference.pikoSplashSavedUrl)
            } else {
                pikoSplashSharedPreference.apply {
                    pikoSplashExpired = pikoSplashData.pikoSplashExpires
                    pikoSplashSavedUrl = pikoSplashData.pikoSplashUrl
                }
                _pikoSplashHomeScreenState.value =
                    PikoSplashHomeScreenState.PikoSplashSuccess(pikoSplashData.pikoSplashUrl)
            }
        }
    }


    sealed class PikoSplashHomeScreenState {
        data object PikoSplashLoading : PikoSplashHomeScreenState()
        data object PikoSplashError : PikoSplashHomeScreenState()
        data class PikoSplashSuccess(val data: String) : PikoSplashHomeScreenState()
        data object PikoSplashNotInternet: PikoSplashHomeScreenState()
    }
}