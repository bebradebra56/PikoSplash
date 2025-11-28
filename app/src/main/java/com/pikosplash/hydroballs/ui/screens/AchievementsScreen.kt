package com.pikosplash.hydroballs.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pikosplash.hydroballs.data.model.Achievement
import com.pikosplash.hydroballs.ui.components.BackgroundBubbles
import com.pikosplash.hydroballs.ui.components.BottomGlow
import com.pikosplash.hydroballs.ui.components.PulsatingBubble
import com.pikosplash.hydroballs.ui.theme.BrightPurple
import com.pikosplash.hydroballs.ui.theme.DarkOverlay
import com.pikosplash.hydroballs.ui.theme.DeepPurple
import com.pikosplash.hydroballs.ui.theme.EmptyBubblePurple
import com.pikosplash.hydroballs.ui.theme.GoldenYellow
import com.pikosplash.hydroballs.ui.theme.MediumPurple
import com.pikosplash.hydroballs.ui.theme.PrimaryWhite
import com.pikosplash.hydroballs.ui.theme.SecondaryGray

@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }
    
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Achievements",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = PrimaryWhite
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress card
            if (!uiState.isLoading) {
                ProgressCard(
                    unlockedCount = uiState.unlockedCount,
                    totalCount = uiState.totalCount
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Achievements grid
            if (!uiState.isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(uiState.achievements) { index, achievement ->
                        AchievementCard(
                            achievement = achievement,
                            index = index
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(
    unlockedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalCount > 0) unlockedCount.toFloat() / totalCount.toFloat() else 0f
    
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
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = PrimaryWhite
                )
                
                Text(
                    text = "$unlockedCount / $totalCount",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = GoldenYellow
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GoldenYellow,
                trackColor = EmptyBubblePurple.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    index: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 100L)
        isVisible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "achievement_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "achievement_alpha"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (achievement.isUnlocked) {
                    Brush.radialGradient(
                        colors = listOf(
                            GoldenYellow.copy(alpha = 0.3f),
                            BrightPurple.copy(alpha = 0.2f)
                        )
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(
                            DarkOverlay,
                            DarkOverlay
                        )
                    )
                }
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon bubble
            if (achievement.isUnlocked) {
                PulsatingBubble(
                    size = 54.dp,
                    color = GoldenYellow
                ) {
                    Text(
                        text = achievement.icon,
                        fontSize = 28.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.3f)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        EmptyBubblePurple.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                ),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                    
                    Text(
                        text = "🔒",
                        fontSize = 28.sp,
                        modifier = Modifier.alpha(0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Title
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = if (achievement.isUnlocked) PrimaryWhite else SecondaryGray,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Description
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryGray.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                maxLines = 2
            )
        }
        
        // Glow effect for unlocked achievements
        if (achievement.isUnlocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(20.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GoldenYellow.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

