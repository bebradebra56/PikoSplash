package com.pikosplash.hydroballs.keopge

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.pikosplash.hydroballs.keopge.presentation.app.PikoSplashApplication

class PikoSplashGlobalLayoutUtil {

    private var pikoSplashMChildOfContent: View? = null
    private var pikoSplashUsableHeightPrevious = 0

    fun pikoSplashAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        pikoSplashMChildOfContent = content.getChildAt(0)

        pikoSplashMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val pikoSplashUsableHeightNow = pikoSplashComputeUsableHeight()
        if (pikoSplashUsableHeightNow != pikoSplashUsableHeightPrevious) {
            val pikoSplashUsableHeightSansKeyboard = pikoSplashMChildOfContent?.rootView?.height ?: 0
            val pikoSplashHeightDifference = pikoSplashUsableHeightSansKeyboard - pikoSplashUsableHeightNow

            if (pikoSplashHeightDifference > (pikoSplashUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(PikoSplashApplication.pikoSplashInputMode)
            } else {
                activity.window.setSoftInputMode(PikoSplashApplication.pikoSplashInputMode)
            }
//            mChildOfContent?.requestLayout()
            pikoSplashUsableHeightPrevious = pikoSplashUsableHeightNow
        }
    }

    private fun pikoSplashComputeUsableHeight(): Int {
        val r = Rect()
        pikoSplashMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}