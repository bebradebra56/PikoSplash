package com.pikosplash.hydroballs.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pikosplash.hydroballs.data.repository.WaterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val glassesConsumed: Int = 0,
    val dailyGoal: Int = 8,
    val percentage: Float = 0f,
    val remainingGlasses: Int = 8,
    val goalReached: Boolean = false
)

class HomeViewModel(private val repository: WaterRepository) : ViewModel() {
    
    private val today = LocalDate.now()
    
    val uiState: StateFlow<HomeUiState> = combine(
        repository.getGlassesForDateFlow(today),
        repository.dailyGoal
    ) { glasses, goal ->
        val percentage = (glasses.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
        HomeUiState(
            glassesConsumed = glasses,
            dailyGoal = goal,
            percentage = percentage,
            remainingGlasses = (goal - glasses).coerceAtLeast(0),
            goalReached = glasses >= goal
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
    
    fun addGlass() {
        viewModelScope.launch {
            repository.addGlass(today)
        }
    }
    
    fun removeGlass() {
        viewModelScope.launch {
            repository.removeGlass(today)
        }
    }
}

