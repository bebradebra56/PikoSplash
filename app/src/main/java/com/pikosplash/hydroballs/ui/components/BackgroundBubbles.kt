package com.pikosplash.hydroballs.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pikosplash.hydroballs.ui.theme.EmptyBubblePurple
import com.pikosplash.hydroballs.ui.theme.LightBubblePurple
import kotlin.random.Random

data class FloatingBubbleData(
    val offsetX: Float,
    val offsetY: Float,
    val size: Float,
    val duration: Int,
    val delay: Int,
    val alpha: Float,
    val color: Color
)

@Composable
fun BackgroundBubbles(modifier: Modifier = Modifier) {
    val bubbles = remember {
        List(15) { index ->
            FloatingBubbleData(
                offsetX = Random.nextFloat() * 400f - 200f,
                offsetY = Random.nextFloat() * 800f - 400f,
                size = Random.nextFloat() * 60f + 30f,
                duration = Random.nextInt(3000, 6000),
                delay = Random.nextInt(0, 2000),
                alpha = Random.nextFloat() * 0.2f + 0.1f,
                color = if (index % 2 == 0) LightBubblePurple else EmptyBubblePurple
            )
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        bubbles.forEach { bubbleData ->
            FloatingBubble(bubbleData)
        }
    }
}

@Composable
private fun FloatingBubble(data: FloatingBubbleData) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating_bubble")
    
    val offsetY by infiniteTransition.animateFloat(
        initialValue = data.offsetY,
        targetValue = data.offsetY - 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(data.duration, easing = EaseInOutQuad, delayMillis = data.delay),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_y"
    )
    
    val offsetX by infiniteTransition.animateFloat(
        initialValue = data.offsetX,
        targetValue = data.offsetX + Random.nextFloat() * 40f - 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(data.duration / 2, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_x"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(data.duration / 3, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_scale"
    )
    
    Box(
        modifier = Modifier
            .offset(x = offsetX.dp, y = offsetY.dp)
            .size((data.size * scale).dp)
            .alpha(data.alpha)
            .blur(12.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        data.color.copy(alpha = 0.6f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
}

@Composable
fun BottomGlow(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            LightBubblePurple.copy(alpha = 0.3f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
    }
}

