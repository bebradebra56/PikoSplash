package com.pikosplash.hydroballs.keopge.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class PikoSplashDataStore : ViewModel(){
    val pikoSplashViList: MutableList<PikoSplashVi> = mutableListOf()
    var pikoSplashIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var pikoSplashContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var pikoSplashView: PikoSplashVi

}