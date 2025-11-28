package com.pikosplash.hydroballs.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pikosplash.hydroballs.ui.theme.BrightPurple
import com.pikosplash.hydroballs.ui.theme.BubbleGlow
import com.pikosplash.hydroballs.ui.theme.DeepPurple
import com.pikosplash.hydroballs.ui.theme.EmptyBubblePurple
import com.pikosplash.hydroballs.ui.theme.PrimaryWhite

@Composable
fun NavigationBar(
    selectedIndex: Int,
    onNavigate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationBubble(
            icon = Icons.Default.Home,
            isSelected = selectedIndex == 0,
            onClick = { onNavigate(0) }
        )
        
        NavigationBubble(
            icon = Icons.Default.BarChart,
            isSelected = selectedIndex == 1,
            onClick = { onNavigate(1) }
        )
        
        NavigationBubble(
            icon = Icons.Default.Settings,
            isSelected = selectedIndex == 2,
            onClick = { onNavigate(2) }
        )
        
        NavigationBubble(
            icon = Icons.Default.EmojiEvents,
            isSelected = selectedIndex == 3,
            onClick = { onNavigate(3) }
        )
    }
}

@Composable
private fun NavigationBubble(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.85f
            isSelected -> 1.15f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "nav_bubble_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.5f,
        animationSpec = tween(300),
        label = "nav_bubble_alpha"
    )
    
    val bubbleColor = if (isSelected) BrightPurple else EmptyBubblePurple
    val iconColor = if (isSelected) DeepPurple else PrimaryWhite
    
    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // Glow effect
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.3f)
                    .alpha(0.6f)
                    .blur(16.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(bubbleColor, Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
        }
        
        // Main bubble
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            bubbleColor.copy(alpha = 0.9f),
                            bubbleColor.copy(alpha = 0.6f)
                        ),
                        center = androidx.compose.ui.geometry.Offset(0.3f, 0.3f)
                    ),
                    shape = CircleShape
                )
        )
        
        // Highlight
        Box(
            modifier = Modifier
                .size(20.dp)
                .offset(x = (-6).dp, y = (-6).dp)
                .alpha(0.5f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(BubbleGlow, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

