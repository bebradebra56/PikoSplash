package com.pikosplash.hydroballs.keopge.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pikosplash.hydroballs.keopge.presentation.app.PikoSplashApplication
import com.pikosplash.hydroballs.keopge.presentation.ui.load.PikoSplashLoadFragment
import org.koin.android.ext.android.inject

class PikoSplashV : Fragment(){

    private lateinit var pikoSplashPhoto: Uri
    private var pikoSplashFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val pikoSplashTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        pikoSplashFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        pikoSplashFilePathFromChrome = null
    }

    private val pikoSplashTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            pikoSplashFilePathFromChrome?.onReceiveValue(arrayOf(pikoSplashPhoto))
            pikoSplashFilePathFromChrome = null
        } else {
            pikoSplashFilePathFromChrome?.onReceiveValue(null)
            pikoSplashFilePathFromChrome = null
        }
    }

    private val pikoSplashDataStore by activityViewModels<PikoSplashDataStore>()


    private val pikoSplashViFun by inject<PikoSplashViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (pikoSplashDataStore.pikoSplashView.canGoBack()) {
                        pikoSplashDataStore.pikoSplashView.goBack()
                        Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "WebView can go back")
                    } else if (pikoSplashDataStore.pikoSplashViList.size > 1) {
                        Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "WebView can`t go back")
                        pikoSplashDataStore.pikoSplashViList.removeAt(pikoSplashDataStore.pikoSplashViList.lastIndex)
                        Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "WebView list size ${pikoSplashDataStore.pikoSplashViList.size}")
                        pikoSplashDataStore.pikoSplashView.destroy()
                        val previousWebView = pikoSplashDataStore.pikoSplashViList.last()
                        pikoSplashAttachWebViewToContainer(previousWebView)
                        pikoSplashDataStore.pikoSplashView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (pikoSplashDataStore.pikoSplashIsFirstCreate) {
            pikoSplashDataStore.pikoSplashIsFirstCreate = false
            pikoSplashDataStore.pikoSplashContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return pikoSplashDataStore.pikoSplashContainerView
        } else {
            return pikoSplashDataStore.pikoSplashContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "onViewCreated")
        if (pikoSplashDataStore.pikoSplashViList.isEmpty()) {
            pikoSplashDataStore.pikoSplashView = PikoSplashVi(requireContext(), object :
                PikoSplashCallBack {
                override fun pikoSplashHandleCreateWebWindowRequest(pikoSplashVi: PikoSplashVi) {
                    pikoSplashDataStore.pikoSplashViList.add(pikoSplashVi)
                    Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "WebView list size = ${pikoSplashDataStore.pikoSplashViList.size}")
                    Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "CreateWebWindowRequest")
                    pikoSplashDataStore.pikoSplashView = pikoSplashVi
                    pikoSplashVi.pikoSplashSetFileChooserHandler { callback ->
                        pikoSplashHandleFileChooser(callback)
                    }
                    pikoSplashAttachWebViewToContainer(pikoSplashVi)
                }

            }, pikoSplashWindow = requireActivity().window).apply {
                pikoSplashSetFileChooserHandler { callback ->
                    pikoSplashHandleFileChooser(callback)
                }
            }
            pikoSplashDataStore.pikoSplashView.pikoSplashFLoad(arguments?.getString(
                PikoSplashLoadFragment.PIKO_SPLASH_D) ?: "")
//            ejvview.fLoad("www.google.com")
            pikoSplashDataStore.pikoSplashViList.add(pikoSplashDataStore.pikoSplashView)
            pikoSplashAttachWebViewToContainer(pikoSplashDataStore.pikoSplashView)
        } else {
            pikoSplashDataStore.pikoSplashViList.forEach { webView ->
                webView.pikoSplashSetFileChooserHandler { callback ->
                    pikoSplashHandleFileChooser(callback)
                }
            }
            pikoSplashDataStore.pikoSplashView = pikoSplashDataStore.pikoSplashViList.last()

            pikoSplashAttachWebViewToContainer(pikoSplashDataStore.pikoSplashView)
        }
        Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "WebView list size = ${pikoSplashDataStore.pikoSplashViList.size}")
    }

    private fun pikoSplashHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        pikoSplashFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Launching file picker")
                    pikoSplashTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "Launching camera")
                    pikoSplashPhoto = pikoSplashViFun.pikoSplashSavePhoto()
                    pikoSplashTakePhoto.launch(pikoSplashPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(PikoSplashApplication.PIKO_SPLASH_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                pikoSplashFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun pikoSplashAttachWebViewToContainer(w: PikoSplashVi) {
        pikoSplashDataStore.pikoSplashContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            pikoSplashDataStore.pikoSplashContainerView.removeAllViews()
            pikoSplashDataStore.pikoSplashContainerView.addView(w)
        }
    }


}