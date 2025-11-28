package com.pikosplash.hydroballs.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pikosplash.hydroballs.ui.theme.BubbleGlow
import com.pikosplash.hydroballs.ui.theme.DeepPurple
import com.pikosplash.hydroballs.ui.theme.EmptyBubblePurple
import com.pikosplash.hydroballs.ui.theme.LightBubblePurple
import com.pikosplash.hydroballs.ui.theme.MediumPurple

@Composable
fun Bubble(
    size: Dp,
    isFilled: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bubble_scale"
    )
    
    val bubbleColor = if (isFilled) LightBubblePurple else EmptyBubblePurple
    val alpha = if (isFilled) 0.8f else 0.4f
    
    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isPressed = true
                        onClick()
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(1.2f)
                .alpha(0.3f)
                .blur(16.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(bubbleColor, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        
        // Main bubble
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            bubbleColor.copy(alpha = 0.9f),
                            bubbleColor.copy(alpha = 0.5f)
                        ),
                        center = androidx.compose.ui.geometry.Offset(0.3f, 0.3f)
                    ),
                    shape = CircleShape
                )
        )
        
        // Inner highlight
        Box(
            modifier = Modifier
                .size(size * 0.4f)
                .offset(x = -(size * 0.15f), y = -(size * 0.15f))
                .alpha(0.6f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(BubbleGlow, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        
        // Content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
fun PulsatingBubble(
    size: Dp,
    color: Color = LightBubblePurple,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsating")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsating_scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsating_alpha"
    )
    
    Box(
        modifier = modifier
            .size(size)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(1.3f)
                .alpha(alpha * 0.4f)
                .blur(20.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(color, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        
        // Main bubble
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = 0.9f),
                            color.copy(alpha = 0.5f)
                        ),
                        center = androidx.compose.ui.geometry.Offset(0.3f, 0.3f)
                    ),
                    shape = CircleShape
                )
        )
        
        // Highlight
        Box(
            modifier = Modifier
                .size(size * 0.4f)
                .offset(x = -(size * 0.15f), y = -(size * 0.15f))
                .alpha(0.7f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(BubbleGlow, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun GlassGlobe(
    percentage: Float,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(1.2f)
                .alpha(0.4f)
                .blur(24.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            com.pikosplash.hydroballs.ui.theme.BrightPurple,
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        // Main globe
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.85f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            com.pikosplash.hydroballs.ui.theme.LightBubblePurple.copy(alpha = 0.9f),
                            com.pikosplash.hydroballs.ui.theme.MediumPurple.copy(alpha = 0.6f)
                        ),
                        center = androidx.compose.ui.geometry.Offset(0.3f, 0.3f)
                    ),
                    shape = CircleShape
                )
        )
        
        // Highlight
        Box(
            modifier = Modifier
                .size(size * 0.5f)
                .offset(x = -(size * 0.15f), y = -(size * 0.15f))
                .alpha(0.7f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BubbleGlow,
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        // Percentage text
        Text(
            text = "${(percentage * 100).toInt()}%",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = (size.value * 0.25f).sp,
                fontWeight = FontWeight.Bold
            ),
            color = DeepPurple
        )
    }
}

@Composable
fun ActionBubble(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = com.pikosplash.hydroballs.ui.theme.BrightPurple
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "action_bubble_scale"
    )
    
    Box(
        modifier = modifier
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
        // Glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(1.15f)
                .alpha(0.5f)
                .blur(16.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(color, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        
        // Main bubble
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = 0.9f),
                            color.copy(alpha = 0.7f)
                        ),
                        center = androidx.compose.ui.geometry.Offset(0.3f, 0.3f)
                    ),
                    shape = CircleShape
                )
        )
        
        // Highlight
        Box(
            modifier = Modifier
                .fillMaxSize(0.4f)
                .offset(x = (-8).dp, y = (-8).dp)
                .alpha(0.8f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(BubbleGlow, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = com.pikosplash.hydroballs.ui.theme.DeepPurple
        )
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

