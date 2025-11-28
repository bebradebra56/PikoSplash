package com.pikosplash.hydroballs.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pikosplash.hydroballs.ui.components.*
import com.pikosplash.hydroballs.ui.theme.BrightPurple
import com.pikosplash.hydroballs.ui.theme.DeepPurple
import com.pikosplash.hydroballs.ui.theme.EmptyBubblePurple
import com.pikosplash.hydroballs.ui.theme.GoldenYellow
import com.pikosplash.hydroballs.ui.theme.MediumPurple
import com.pikosplash.hydroballs.ui.theme.PrimaryWhite
import com.pikosplash.hydroballs.ui.theme.SecondaryGray

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSplash by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepPurple, MediumPurple)
                )
            )
    ) {
        // Background bubbles
        BackgroundBubbles()
        BottomGlow()
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Piko Splash 💧",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = PrimaryWhite
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Subtitle
            val subtitleText = when {
                uiState.goalReached -> "Goal reached! Great job! 🎉"
                uiState.remainingGlasses == 1 -> "Drink 1 more glass!"
                else -> "Drink ${uiState.remainingGlasses} more glasses!"
            }
            
            Text(
                text = subtitleText,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryGray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Central progress globe
            if (uiState.goalReached) {
                PulsatingBubble(
                    size = 160.dp,
                    color = GoldenYellow
                ) {
                    Text(
                        text = "100%",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = DeepPurple
                    )
                }
            } else {
                GlassGlobe(
                    percentage = uiState.percentage,
                    size = 160.dp
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Progress text
            Text(
                text = "${uiState.glassesConsumed} / ${uiState.dailyGoal} glasses",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                ),
                color = PrimaryWhite
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Small bubbles representing glasses
            GlassBubbleRow(
                totalGlasses = uiState.dailyGoal,
                consumedGlasses = uiState.glassesConsumed
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Add glass button
            ActionBubble(
                text = "+ Add Glass",
                onClick = {
                    viewModel.addGlass()
                    showSplash = true
                },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(60.dp),
                color = BrightPurple
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Remove glass button (smaller, less prominent)
            if (uiState.glassesConsumed > 0) {
                ActionBubble(
                    text = "- Remove",
                    onClick = { viewModel.removeGlass() },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(48.dp),
                    color = EmptyBubblePurple
                )
            }
            
            Spacer(modifier = Modifier.height(90.dp))
        }
        
        // Splash animation when adding glass
        if (showSplash) {
            SplashAnimation(onComplete = { showSplash = false })
        }
    }
}

@Composable
private fun GlassBubbleRow(
    totalGlasses: Int,
    consumedGlasses: Int,
    modifier: Modifier = Modifier
) {
    // Show max 8 bubbles in 2 rows
    val displayGlasses = minOf(totalGlasses, 8)
    val bubbleSize = 28.dp
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // First row - up to 4 bubbles
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(minOf(displayGlasses, 4)) { index ->
                Bubble(
                    size = bubbleSize,
                    isFilled = index < consumedGlasses
                )
            }
        }
        
        // Second row - remaining bubbles
        if (displayGlasses > 4) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(displayGlasses - 4) { index ->
                    val globalIndex = index + 4
                    Bubble(
                        size = bubbleSize,
                        isFilled = globalIndex < consumedGlasses
                    )
                }
                
                if (totalGlasses > 8) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${totalGlasses - 8}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SplashAnimation(onComplete: () -> Unit) {
    var bubbles by remember { mutableStateOf(List(6) { 0f }) }
    
    LaunchedEffect(Unit) {
        // Animate bubbles rising
        repeat(6) { index ->
            kotlinx.coroutines.delay(index * 40L)
            bubbles = bubbles.toMutableList().apply { set(index, 1f) }
        }
        kotlinx.coroutines.delay(400)
        onComplete()
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        bubbles.forEachIndexed { index, progress ->
            if (progress > 0f) {
                val offsetX = (index % 3) * 80f - 80f
                val startY = 800f
                
                val animatedY by animateFloatAsState(
                    targetValue = if (progress > 0f) -200f else startY,
                    animationSpec = tween(600, easing = FastOutSlowInEasing),
                    label = "splash_bubble_$index"
                )
                
                val animatedAlpha by animateFloatAsState(
                    targetValue = if (progress > 0f && animatedY > 200f) 1f else 0f,
                    animationSpec = tween(300),
                    label = "splash_alpha_$index"
                )
                
                Box(
                    modifier = Modifier
                        .offset(x = offsetX.dp, y = animatedY.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Bubble(
                        size = 32.dp,
                        isFilled = true,
                        modifier = Modifier.alpha(animatedAlpha)
                    )
                }
            }
        }
    }
}

