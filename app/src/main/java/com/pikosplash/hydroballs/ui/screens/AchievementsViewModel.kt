package com.pikosplash.hydroballs.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pikosplash.hydroballs.data.model.Achievement
import com.pikosplash.hydroballs.data.repository.WaterRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AchievementsUiState(
    val achievements: List<Achievement> = emptyList(),
    val unlockedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = true
)

class AchievementsViewModel(private val repository: WaterRepository) : ViewModel() {
    
    private val _refreshTrigger = MutableStateFlow(0)
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AchievementsUiState> = _refreshTrigger.flatMapLatest {
        flow {
            emit(AchievementsUiState(isLoading = true))
            val achievements = repository.getUnlockedAchievements()
            val unlockedCount = achievements.count { it.isUnlocked }
            emit(
                AchievementsUiState(
                    achievements = achievements,
                    unlockedCount = unlockedCount,
                    totalCount = achievements.size,
                    isLoading = false
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AchievementsUiState()
    )
    
    fun refresh() {
        viewModelScope.launch {
            _refreshTrigger.value++
        }
    }
}

