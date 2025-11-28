package com.pikosplash.hydroballs.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pikosplash.hydroballs.data.repository.WaterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val dailyGoal: Int = 8,
    val reminderEnabled: Boolean = false,
    val reminderInterval: Int = 2,
    val soundEnabled: Boolean = true
)

class SettingsViewModel(private val repository: WaterRepository) : ViewModel() {
    
    val uiState: StateFlow<SettingsUiState> = combine(
        repository.dailyGoal,
        repository.reminderEnabled,
        repository.reminderInterval,
        repository.soundEnabled
    ) { goal, reminderEnabled, interval, soundEnabled ->
        SettingsUiState(
            dailyGoal = goal,
            reminderEnabled = reminderEnabled,
            reminderInterval = interval,
            soundEnabled = soundEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )
    
    fun updateDailyGoal(goal: Int) {
        viewModelScope.launch {
            repository.saveDailyGoal(goal)
        }
    }
    
    fun toggleReminder(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveReminderEnabled(enabled)
        }
    }
    
    fun updateReminderInterval(hours: Int) {
        viewModelScope.launch {
            repository.saveReminderInterval(hours)
        }
    }
    
    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveSoundEnabled(enabled)
        }
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }
}

