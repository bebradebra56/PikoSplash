package com.pikosplash.hydroballs.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pikosplash.hydroballs.data.model.WaterEntry
import com.pikosplash.hydroballs.ui.components.BackgroundBubbles
import com.pikosplash.hydroballs.ui.components.BottomGlow
import com.pikosplash.hydroballs.ui.components.Bubble
import com.pikosplash.hydroballs.ui.components.PulsatingBubble
import com.pikosplash.hydroballs.ui.theme.BrightPurple
import com.pikosplash.hydroballs.ui.theme.DarkOverlay
import com.pikosplash.hydroballs.ui.theme.DeepPurple
import com.pikosplash.hydroballs.ui.theme.EmptyBubblePurple
import com.pikosplash.hydroballs.ui.theme.GoldenYellow
import com.pikosplash.hydroballs.ui.theme.LightBubblePurple
import com.pikosplash.hydroballs.ui.theme.MediumPurple
import com.pikosplash.hydroballs.ui.theme.PrimaryWhite
import com.pikosplash.hydroballs.ui.theme.SecondaryGray
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepPurple, MediumPurple)
                )
            )
    ) {
        BackgroundBubbles()
        BottomGlow()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Water Calendar",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = PrimaryWhite
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Time range toggle
            TimeRangeToggle(
                selectedRange = uiState.timeRange,
                onRangeSelected = { 
                    viewModel.setTimeRange(it)
                    selectedDate = null
                }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Calendar with bubbles
            if (!uiState.isLoading && uiState.entries.isNotEmpty()) {
                BubbleCalendar(
                    entries = uiState.entries,
                    dailyGoal = uiState.dailyGoal,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    timeRange = uiState.timeRange
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Statistics section
            if (selectedDate != null) {
                val selectedEntry = uiState.entries.find { it.date == selectedDate }
                if (selectedEntry != null) {
                    DayDetailCard(
                        entry = selectedEntry,
                        dailyGoal = uiState.dailyGoal
                    )
                }
            } else {
                // Summary stats
                WeeklySummaryCard(
                    entries = uiState.entries,
                    dailyGoal = uiState.dailyGoal,
                    bestDay = uiState.bestDay
                )
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun TimeRangeToggle(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(DarkOverlay)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ToggleButton(
            text = "Week",
            isSelected = selectedRange == TimeRange.WEEK,
            onClick = { onRangeSelected(TimeRange.WEEK) }
        )
        
        ToggleButton(
            text = "Month",
            isSelected = selectedRange == TimeRange.MONTH,
            onClick = { onRangeSelected(TimeRange.MONTH) }
        )
    }
}

@Composable
private fun ToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) BrightPurple else androidx.compose.ui.graphics.Color.Transparent,
        animationSpec = tween(300),
        label = "toggle_bg"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) DeepPurple else SecondaryGray,
        animationSpec = tween(300),
        label = "toggle_text"
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = textColor
        )
    }
}

@Composable
private fun BubbleCalendar(
    entries: List<WaterEntry>,
    dailyGoal: Int,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    timeRange: TimeRange,
    modifier: Modifier = Modifier
) {
    // Create map for quick lookup
    val entriesMap = entries.associateBy { it.date }
    
    // Calculate calendar days to show
    val today = LocalDate.now()
    val calendarDays = remember(timeRange, today) {
        when (timeRange) {
            TimeRange.WEEK -> {
                // Show current week (Monday to Sunday)
                val monday = today.minusDays((today.dayOfWeek.value - 1).toLong())
                (0..6).map { monday.plusDays(it.toLong()) }
            }
            TimeRange.MONTH -> {
                // Show full current month
                val firstDayOfMonth = today.withDayOfMonth(1)
                val lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth())
                val days = mutableListOf<LocalDate>()
                var current = firstDayOfMonth
                while (!current.isAfter(lastDayOfMonth)) {
                    days.add(current)
                    current = current.plusDays(1)
                }
                days
            }
        }
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Days of week header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryGray,
                    fontSize = 11.sp,
                    modifier = Modifier.width(44.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Calculate empty cells at start for month view
        val emptyCellsAtStart = if (timeRange == TimeRange.MONTH) {
            val firstDay = calendarDays.firstOrNull()
            if (firstDay != null) {
                firstDay.dayOfWeek.value - 1
            } else 0
        } else 0
        
        // Calculate grid height
        val totalCells = emptyCellsAtStart + calendarDays.size
        val rows = (totalCells + 6) / 7
        val gridHeight = (rows * 52).dp
        
        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(gridHeight),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            // Empty cells at start
            items(emptyCellsAtStart) {
                Spacer(modifier = Modifier.size(40.dp))
            }
            
            // Calendar day bubbles
            items(calendarDays) { date ->
                val entry = entriesMap[date] ?: WaterEntry(date, 0)
                CalendarDayBubble(
                    entry = entry,
                    dailyGoal = dailyGoal,
                    isSelected = selectedDate == date,
                    onClick = {
                        onDateSelected(if (selectedDate == date) null else date)
                    },
                    isToday = date == today
                )
            }
        }
    }
}

@Composable
private fun CalendarDayBubble(
    entry: WaterEntry,
    dailyGoal: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    isToday: Boolean = false,
    modifier: Modifier = Modifier
) {
    val fillPercentage = if (dailyGoal > 0) {
        (entry.glassesCount.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)
    } else 0f
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "day_bubble_scale"
    )
    
    val bubbleColor = when {
        entry.glassesCount == 0 -> EmptyBubblePurple.copy(alpha = 0.3f)
        fillPercentage >= 1f -> GoldenYellow
        fillPercentage >= 0.7f -> LightBubblePurple
        else -> LightBubblePurple.copy(alpha = 0.6f)
    }
    
    Column(
        modifier = modifier
            .width(44.dp)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp)
        ) {
            if (fillPercentage >= 1f) {
                PulsatingBubble(
                    size = 40.dp,
                    color = bubbleColor
                ) {
                    Text(
                        text = entry.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                                    color = DeepPurple,
                        fontSize = 12.sp
                    )
                }
            } else {
                Bubble(
                    size = 40.dp,
                    isFilled = entry.glassesCount > 0,
                    modifier = Modifier.alpha(if (entry.glassesCount > 0) 0.9f else 0.4f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = entry.date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (entry.glassesCount > 0) PrimaryWhite else SecondaryGray,
                            fontSize = 12.sp
                        )
                        
                        if (entry.glassesCount > 0) {
                            Text(
                                text = "•".repeat(minOf(entry.glassesCount, 3)),
                                style = MaterialTheme.typography.bodySmall,
                                color = BrightPurple,
                                fontSize = 8.sp,
                                lineHeight = 8.sp
                            )
                        }
                    }
                }
            }
            
            // Selection ring or today indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .alpha(0.6f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    androidx.compose.ui.graphics.Color.Transparent,
                                    BrightPurple,
                                    androidx.compose.ui.graphics.Color.Transparent
                                )
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            } else if (isToday) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .alpha(0.4f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    androidx.compose.ui.graphics.Color.Transparent,
                                    GoldenYellow,
                                    androidx.compose.ui.graphics.Color.Transparent
                                )
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun DayDetailCard(
    entry: WaterEntry,
    dailyGoal: Int,
    modifier: Modifier = Modifier
) {
    val percentage = if (dailyGoal > 0) {
        ((entry.glassesCount.toFloat() / dailyGoal.toFloat()) * 100).toInt()
    } else 0
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BrightPurple.copy(alpha = 0.25f),
                        LightBubblePurple.copy(alpha = 0.15f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Date header
            Text(
                text = entry.date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd")),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = PrimaryWhite,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Glasses consumed
                StatBubble(
                    value = "${entry.glassesCount}",
                    label = "Glasses",
                    color = LightBubblePurple
                )
                
                // Percentage
                StatBubble(
                    value = "$percentage%",
                    label = "Goal",
                    color = if (percentage >= 100) GoldenYellow else BrightPurple
                )
                
                // Status
                StatBubble(
                    value = if (percentage >= 100) "✓" else "...",
                    label = "Status",
                    color = if (percentage >= 100) GoldenYellow else EmptyBubblePurple
                )
            }
        }
    }
}

@Composable
private fun StatBubble(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Bubble(
                size = 60.dp,
                isFilled = true
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryGray,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun WeeklySummaryCard(
    entries: List<WaterEntry>,
    dailyGoal: Int,
    bestDay: WaterEntry?,
    modifier: Modifier = Modifier
) {
    val totalGlasses = entries.sumOf { it.glassesCount }
    val averageGlasses = if (entries.isNotEmpty()) totalGlasses / entries.size else 0
    val daysCompleted = entries.count { it.glassesCount >= dailyGoal }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        GoldenYellow.copy(alpha = 0.2f),
                        BrightPurple.copy(alpha = 0.2f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = PrimaryWhite
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    value = "$totalGlasses",
                    label = "Total\nGlasses",
                    icon = "💧"
                )
                
                SummaryItem(
                    value = "$averageGlasses",
                    label = "Average\nper Day",
                    icon = "📊"
                )
                
                SummaryItem(
                    value = "$daysCompleted",
                    label = "Days\nCompleted",
                    icon = "✅"
                )
            }
            
            if (bestDay != null && bestDay.glassesCount > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Best Day 🏆",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = GoldenYellow,
                            fontSize = 16.sp
                        )
                        
                        Text(
                            text = bestDay.date.format(DateTimeFormatter.ofPattern("MMM dd")),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryGray,
                            fontSize = 13.sp
                        )
                    }
                    
                    Box(contentAlignment = Alignment.Center) {
                        Bubble(
                            size = 50.dp,
                            isFilled = true
                        ) {
                            Text(
                                text = "${bestDay.glassesCount}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = GoldenYellow,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(
    value: String,
    label: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = BrightPurple,
            fontSize = 22.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryGray,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}

