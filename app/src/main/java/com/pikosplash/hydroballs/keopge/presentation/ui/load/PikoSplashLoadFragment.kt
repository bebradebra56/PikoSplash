package com.pikosplash.hydroballs.keopge.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.pikosplash.hydroballs.MainActivity
import com.pikosplash.hydroballs.R
import com.pikosplash.hydroballs.databinding.FragmentLoadPikoSplashBinding
import com.pikosplash.hydroballs.keopge.data.shar.PikoSplashSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class PikoSplashLoadFragment : Fragment(R.layout.fragment_load_piko_splash) {
    private lateinit var pikoSplashLoadBinding: FragmentLoadPikoSplashBinding

    private val pikoSplashLoadViewModel by viewModel<PikoSplashLoadViewModel>()

    private val pikoSplashSharedPreference by inject<PikoSplashSharedPreference>()

    private var pikoSplashUrl = ""

    private val pikoSplashRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pikoSplashNavigateToSuccess(pikoSplashUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                pikoSplashSharedPreference.pikoSplashNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                pikoSplashNavigateToSuccess(pikoSplashUrl)
            } else {
                pikoSplashNavigateToSuccess(pikoSplashUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pikoSplashLoadBinding = FragmentLoadPikoSplashBinding.bind(view)

        pikoSplashLoadBinding.pikoSplashGrandButton.setOnClickListener {
            val pikoSplashPermission = Manifest.permission.POST_NOTIFICATIONS
            pikoSplashRequestNotificationPermission.launch(pikoSplashPermission)
            pikoSplashSharedPreference.pikoSplashNotificationRequestedBefore = true
        }

        pikoSplashLoadBinding.pikoSplashSkipButton.setOnClickListener {
            pikoSplashSharedPreference.pikoSplashNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            pikoSplashNavigateToSuccess(pikoSplashUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pikoSplashLoadViewModel.pikoSplashHomeScreenState.collect {
                    when (it) {
                        is PikoSplashLoadViewModel.PikoSplashHomeScreenState.PikoSplashLoading -> {

                        }

                        is PikoSplashLoadViewModel.PikoSplashHomeScreenState.PikoSplashError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is PikoSplashLoadViewModel.PikoSplashHomeScreenState.PikoSplashSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val pikoSplashPermission = Manifest.permission.POST_NOTIFICATIONS
                                val pikoSplashPermissionRequestedBefore = pikoSplashSharedPreference.pikoSplashNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), pikoSplashPermission) == PackageManager.PERMISSION_GRANTED) {
                                    pikoSplashNavigateToSuccess(it.data)
                                } else if (!pikoSplashPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > pikoSplashSharedPreference.pikoSplashNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    pikoSplashLoadBinding.pikoSplashNotiGroup.visibility = View.VISIBLE
                                    pikoSplashLoadBinding.pikoSplashLoadingGroup.visibility = View.GONE
                                    pikoSplashUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(pikoSplashPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > pikoSplashSharedPreference.pikoSplashNotificationRequest) {
                                        pikoSplashLoadBinding.pikoSplashNotiGroup.visibility = View.VISIBLE
                                        pikoSplashLoadBinding.pikoSplashLoadingGroup.visibility = View.GONE
                                        pikoSplashUrl = it.data
                                    } else {
                                        pikoSplashNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    pikoSplashNavigateToSuccess(it.data)
                                }
                            } else {
                                pikoSplashNavigateToSuccess(it.data)
                            }
                        }

                        PikoSplashLoadViewModel.PikoSplashHomeScreenState.PikoSplashNotInternet -> {
                            pikoSplashLoadBinding.pikoSplashStateGroup.visibility = View.VISIBLE
                            pikoSplashLoadBinding.pikoSplashLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun pikoSplashNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_pikoSplashLoadFragment_to_pikoSplashV,
            bundleOf(PIKO_SPLASH_D to data)
        )
    }

    companion object {
        const val PIKO_SPLASH_D = "pikoSplashData"
    }
}