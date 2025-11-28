package com.pikosplash.hydroballs.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pikosplash.hydroballs.data.model.WaterEntry
import com.pikosplash.hydroballs.data.repository.WaterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class TimeRange {
    WEEK, MONTH
}

data class StatisticsUiState(
    val entries: List<WaterEntry> = emptyList(),
    val timeRange: TimeRange = TimeRange.WEEK,
    val maxGlasses: Int = 0,
    val bestDay: WaterEntry? = null,
    val dailyGoal: Int = 8,
    val isLoading: Boolean = true
)

class StatisticsViewModel(private val repository: WaterRepository) : ViewModel() {
    
    private val _timeRange = MutableStateFlow(TimeRange.WEEK)
    
    val uiState: StateFlow<StatisticsUiState> = combine(
        _timeRange,
        repository.dailyGoal
    ) { timeRange, goal ->
        val endDate = LocalDate.now()
        val startDate = when (timeRange) {
            TimeRange.WEEK -> endDate.minusDays(6)
            TimeRange.MONTH -> endDate.minusDays(29)
        }
        
        val entries = repository.getEntriesForDateRange(startDate, endDate)
        val maxGlasses = entries.maxOfOrNull { it.glassesCount } ?: 0
        val bestDay = entries.maxByOrNull { it.glassesCount }
        
        StatisticsUiState(
            entries = entries,
            timeRange = timeRange,
            maxGlasses = maxGlasses,
            bestDay = bestDay,
            dailyGoal = goal,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )
    
    fun setTimeRange(timeRange: TimeRange) {
        viewModelScope.launch {
            _timeRange.value = timeRange
        }
    }
}

