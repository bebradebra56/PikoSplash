package com.pikosplash.hydroballs

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.pikosplash.hydroballs.data.repository.WaterRepository
import com.pikosplash.hydroballs.notification.ReminderScheduler
import com.pikosplash.hydroballs.ui.components.NavigationBar
import com.pikosplash.hydroballs.ui.screens.*
import com.pikosplash.hydroballs.ui.theme.DeepPurple
import com.pikosplash.hydroballs.ui.theme.HydroBallsTheme
import com.pikosplash.hydroballs.ui.theme.MediumPurple
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(DeepPurple.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(DeepPurple.toArgb())
        )

        
        setContent {
            HydroBallsTheme {
                HydroBallsApp()
            }
        }
    }
}

@Composable
fun HydroBallsApp() {
    val context = LocalContext.current
    val repository = remember { WaterRepository(context) }
    
    // ViewModels
    val homeViewModel = remember { HomeViewModel(repository) }
    val statisticsViewModel = remember { StatisticsViewModel(repository) }
    val settingsViewModel = remember { SettingsViewModel(repository) }
    val achievementsViewModel = remember { AchievementsViewModel(repository) }
    
    var selectedScreen by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    
    // Handle reminder scheduling
    LaunchedEffect(Unit) {
        scope.launch {
            repository.reminderEnabled.collect { enabled ->
                if (enabled) {
                    val interval = repository.reminderInterval.first()
                    ReminderScheduler.scheduleReminder(context, interval)
                } else {
                    ReminderScheduler.cancelReminder(context)
                }
            }
        }
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                selectedIndex = selectedScreen,
                onNavigate = { selectedScreen = it },
                modifier = Modifier.navigationBarsPadding()
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DeepPurple, MediumPurple)
                    )
                )
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(paddingValues)
        ) {
            when (selectedScreen) {
                0 -> HomeScreen(viewModel = homeViewModel)
                1 -> StatisticsScreen(viewModel = statisticsViewModel)
                2 -> SettingsScreen(viewModel = settingsViewModel)
                3 -> AchievementsScreen(viewModel = achievementsViewModel)
            }
        }
    }
}

