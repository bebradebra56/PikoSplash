package com.pikosplash.hydroballs.data.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val icon: String
)

object AchievementsList {
    val achievements = listOf(
        Achievement(
            id = "first_glass",
            title = "First Drop",
            description = "Drink your first glass of water",
            icon = "💧"
        ),
        Achievement(
            id = "daily_goal",
            title = "Daily Champion",
            description = "Reach your daily goal",
            icon = "🏆"
        ),
        Achievement(
            id = "three_days",
            title = "3 Days Streak",
            description = "Reach your goal 3 days in a row",
            icon = "🔥"
        ),
        Achievement(
            id = "week_streak",
            title = "Week Warrior",
            description = "Reach your goal 7 days in a row",
            icon = "⭐"
        ),
        Achievement(
            id = "hundred_glasses",
            title = "Hundred Club",
            description = "Drink 100 glasses total",
            icon = "💯"
        ),
        Achievement(
            id = "two_weeks",
            title = "Two Weeks Strong",
            description = "Reach your goal 14 days in a row",
            icon = "✨"
        )
    )
}

