package com.pikosplash.hydroballs.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pikosplash.hydroballs.data.model.Achievement
import com.pikosplash.hydroballs.data.model.AchievementsList
import com.pikosplash.hydroballs.data.model.WaterEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hydro_balls_prefs")

class WaterRepository(private val context: Context) {
    
    private val dailyGoalKey = intPreferencesKey("daily_goal")
    private val reminderEnabledKey = booleanPreferencesKey("reminder_enabled")
    private val reminderIntervalKey = intPreferencesKey("reminder_interval")
    private val soundEnabledKey = booleanPreferencesKey("sound_enabled")
    
    // Daily goal flow
    val dailyGoal: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[dailyGoalKey] ?: 8
    }
    
    // Reminder settings flow
    val reminderEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[reminderEnabledKey] ?: false
    }
    
    val reminderInterval: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[reminderIntervalKey] ?: 2
    }
    
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[soundEnabledKey] ?: true
    }
    
    // Save daily goal
    suspend fun saveDailyGoal(goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[dailyGoalKey] = goal
        }
    }
    
    // Save reminder settings
    suspend fun saveReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[reminderEnabledKey] = enabled
        }
    }
    
    suspend fun saveReminderInterval(hours: Int) {
        context.dataStore.edit { preferences ->
            preferences[reminderIntervalKey] = hours
        }
    }
    
    suspend fun saveSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[soundEnabledKey] = enabled
        }
    }
    
    // Water entry management
    suspend fun addGlass(date: LocalDate) {
        val key = intPreferencesKey("water_${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}")
        context.dataStore.edit { preferences ->
            val current = preferences[key] ?: 0
            preferences[key] = current + 1
        }
    }
    
    suspend fun removeGlass(date: LocalDate) {
        val key = intPreferencesKey("water_${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}")
        context.dataStore.edit { preferences ->
            val current = preferences[key] ?: 0
            if (current > 0) {
                preferences[key] = current - 1
            }
        }
    }
    
    suspend fun getGlassesForDate(date: LocalDate): Int {
        val key = intPreferencesKey("water_${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}")
        return context.dataStore.data.first()[key] ?: 0
    }
    
    fun getGlassesForDateFlow(date: LocalDate): Flow<Int> {
        val key = intPreferencesKey("water_${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}")
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: 0
        }
    }
    
    suspend fun getEntriesForDateRange(startDate: LocalDate, endDate: LocalDate): List<WaterEntry> {
        val entries = mutableListOf<WaterEntry>()
        var currentDate = startDate
        
        while (!currentDate.isAfter(endDate)) {
            val glasses = getGlassesForDate(currentDate)
            entries.add(WaterEntry(currentDate, glasses))
            currentDate = currentDate.plusDays(1)
        }
        
        return entries
    }
    
    // Achievements
    suspend fun getUnlockedAchievements(): List<Achievement> {
        val totalGlasses = getTotalGlasses()
        val currentStreak = getCurrentStreak()
        val reachedGoalToday = hasReachedGoalToday()
        
        return AchievementsList.achievements.map { achievement ->
            val isUnlocked = when (achievement.id) {
                "first_glass" -> totalGlasses >= 1
                "daily_goal" -> reachedGoalToday
                "three_days" -> currentStreak >= 3
                "week_streak" -> currentStreak >= 7
                "hundred_glasses" -> totalGlasses >= 100
                "two_weeks" -> currentStreak >= 14
                else -> false
            }
            achievement.copy(isUnlocked = isUnlocked)
        }
    }
    
    private suspend fun getTotalGlasses(): Int {
        var total = 0
        val preferences = context.dataStore.data.first()
        
        preferences.asMap().forEach { (key, value) ->
            if (key.name.startsWith("water_") && value is Int) {
                total += value
            }
        }
        
        return total
    }
    
    private suspend fun getCurrentStreak(): Int {
        var streak = 0
        var currentDate = LocalDate.now()
        val goal = dailyGoal.first()
        
        while (true) {
            val glasses = getGlassesForDate(currentDate)
            if (glasses >= goal) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else {
                break
            }
        }
        
        return streak
    }
    
    private suspend fun hasReachedGoalToday(): Boolean {
        val goal = dailyGoal.first()
        val todayGlasses = getGlassesForDate(LocalDate.now())
        return todayGlasses >= goal
    }
    
    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            val keysToRemove = preferences.asMap().keys.filter { it.name.startsWith("water_") }
            keysToRemove.forEach { key ->
                preferences.remove(key)
            }
        }
    }
}

