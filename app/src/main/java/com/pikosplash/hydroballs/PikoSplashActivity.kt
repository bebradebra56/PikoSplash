package com.pikosplash.hydroballs

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.pikosplash.hydroballs.keopge.PikoSplashGlobalLayoutUtil
import com.pikosplash.hydroballs.keopge.presentation.app.PikoSplashApplication
import com.pikosplash.hydroballs.keopge.presentation.pushhandler.PikoSplashPushHandler
import com.pikosplash.hydroballs.keopge.pikoSplashSetupSystemBars
import org.koin.android.ext.android.inject

class PikoSplashActivity : AppCompatActivity() {
    private val pikoSplashPushHandler by inject<PikoSplashPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pikoSplashSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_piko_splash)

        val pikoSplashRootView = findViewById<View>(android.R.id.content)
        PikoSplashGlobalLayoutUtil().pikoSplashAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(pikoSplashRootView) { pikoSplashView, pikoSplashInsets ->
            val pikoSplashSystemBars = pikoSplashInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val pikoSplashDisplayCutout = pikoSplashInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val pikoSplashIme = pikoSplashInsets.getInsets(WindowInsetsCompat.Type.ime())


            val pikoSplashTopPadding = maxOf(pikoSplashSystemBars.top, pikoSplashDisplayCutout.top)
            val pikoSplashLeftPadding = maxOf(pikoSplashSystemBars.left, pikoSplashDisplayCutout.left)
            val pikoSplashRightPadding = maxOf(pikoSplashSystemBars.right, pikoSplashDisplayCutout.right)
            window.setSoftInputMode(PikoSplashApplication.pikoSplashInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "ADJUST PUN")
                val pikoSplashBottomInset = maxOf(pikoSplashSystemBars.bottom, pikoSplashDisplayCutout.bottom)

                pikoSplashView.setPadding(pikoSplashLeftPadding, pikoSplashTopPadding, pikoSplashRightPadding, 0)

                pikoSplashView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = pikoSplashBottomInset
                }
            } else {
                Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "ADJUST RESIZE")

                val pikoSplashBottomInset = maxOf(pikoSplashSystemBars.bottom, pikoSplashDisplayCutout.bottom, pikoSplashIme.bottom)

                pikoSplashView.setPadding(pikoSplashLeftPadding, pikoSplashTopPadding, pikoSplashRightPadding, 0)

                pikoSplashView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = pikoSplashBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Activity onCreate()")
        pikoSplashPushHandler.pikoSplashHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            pikoSplashSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        pikoSplashSetupSystemBars()
    }
}