package com.pikosplash.hydroballs.keopge.presentation.di

import com.pikosplash.hydroballs.keopge.data.repo.PikoSplashRepository
import com.pikosplash.hydroballs.keopge.data.shar.PikoSplashSharedPreference
import com.pikosplash.hydroballs.keopge.data.utils.PikoSplashPushToken
import com.pikosplash.hydroballs.keopge.data.utils.PikoSplashSystemService
import com.pikosplash.hydroballs.keopge.domain.usecases.PikoSplashGetAllUseCase
import com.pikosplash.hydroballs.keopge.presentation.pushhandler.PikoSplashPushHandler
import com.pikosplash.hydroballs.keopge.presentation.ui.load.PikoSplashLoadViewModel
import com.pikosplash.hydroballs.keopge.presentation.ui.view.PikoSplashViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val pikoSplashModule = module {
    factory {
        PikoSplashPushHandler()
    }
    single {
        PikoSplashRepository()
    }
    single {
        PikoSplashSharedPreference(get())
    }
    factory {
        PikoSplashPushToken()
    }
    factory {
        PikoSplashSystemService(get())
    }
    factory {
        PikoSplashGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        PikoSplashViFun(get())
    }
    viewModel {
        PikoSplashLoadViewModel(get(), get(), get())
    }
}