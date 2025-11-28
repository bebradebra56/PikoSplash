package com.pikosplash.hydroballs.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pikosplash.hydroballs.ui.components.BackgroundBubbles
import com.pikosplash.hydroballs.ui.components.BottomGlow
import com.pikosplash.hydroballs.ui.components.Bubble
import com.pikosplash.hydroballs.ui.theme.BrightPurple
import com.pikosplash.hydroballs.ui.theme.DarkOverlay
import com.pikosplash.hydroballs.ui.theme.DeepPurple
import com.pikosplash.hydroballs.ui.theme.EmptyBubblePurple
import com.pikosplash.hydroballs.ui.theme.MediumPurple
import com.pikosplash.hydroballs.ui.theme.PrimaryWhite
import com.pikosplash.hydroballs.ui.theme.RedAlert
import com.pikosplash.hydroballs.ui.theme.SecondaryGray

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showClearDialog by remember { mutableStateOf(false) }
    
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
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = PrimaryWhite
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Daily Goal Setting
            SettingsCard(
                title = "Daily Goal",
                icon = Icons.Default.LocalDrink
            ) {
                Column {
                    Text(
                        text = "Target glasses per day",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryGray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    GoalSelector(
                        currentGoal = uiState.dailyGoal,
                        onGoalChange = { viewModel.updateDailyGoal(it) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            PolicyCard(
                "Privacy Policy",
                icon = Icons.Default.Policy
            )

            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Clear Data
            SettingsCard(
                title = "Data",
                icon = Icons.Default.DeleteForever,
                isDanger = true
            ) {
                Column {
                    Text(
                        text = "Clear all water tracking data",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryGray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DangerButton(
                        text = "Clear All Data",
                        onClick = { showClearDialog = true }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
        
        if (showClearDialog) {
            ConfirmDialog(
                title = "Clear All Data?",
                message = "This will permanently delete all your water tracking history. This action cannot be undone.",
                onConfirm = {
                    viewModel.clearAllData()
                    showClearDialog = false
                },
                onDismiss = { showClearDialog = false }
            )
        }
    }
}

@Composable
private fun PolicyCard(
    title: String,
    icon: ImageVector,
    isDanger: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isDanger) {
                    Color(0x20FF5555)
                } else {
                    DarkOverlay
                }
            )
            .padding(16.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pikosplash.com/privacy-policy.html"))
                context.startActivity(intent)
            }
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDanger) RedAlert else BrightPurple,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = PrimaryWhite
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    isDanger: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isDanger) {
                    Color(0x20FF5555)
                } else {
                    DarkOverlay
                }
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDanger) RedAlert else BrightPurple,
                    modifier = Modifier.size(24.dp)
                )
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = PrimaryWhite
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
private fun GoalSelector(
    currentGoal: Int,
    onGoalChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(6, 8, 10, 12).forEach { goal ->
            GoalBubble(
                goal = goal,
                isSelected = currentGoal == goal,
                onClick = { onGoalChange(goal) }
            )
        }
    }
}

@Composable
private fun GoalBubble(
    goal: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.9f
            isSelected -> 1.08f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "goal_bubble_scale"
    )
    
    Box(
        modifier = modifier
            .size(60.dp)
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
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.3f)
                    .alpha(0.5f)
                    .blur(16.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(BrightPurple, Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            (if (isSelected) BrightPurple else EmptyBubblePurple).copy(alpha = 0.9f),
                            (if (isSelected) BrightPurple else EmptyBubblePurple).copy(alpha = 0.6f)
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        Text(
            text = "$goal",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = if (isSelected) DeepPurple else PrimaryWhite
        )
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun IntervalSelector(
    currentInterval: Int,
    onIntervalChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(1, 2, 3, 4).forEach { interval ->
            SmallBubble(
                value = interval,
                isSelected = currentInterval == interval,
                onClick = { onIntervalChange(interval) }
            )
        }
    }
}

@Composable
private fun SmallBubble(
    value: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        label = "small_bubble_scale"
    )
    
    Box(
        modifier = modifier
            .size(44.dp)
            .scale(scale)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Bubble(
            size = 44.dp,
            isFilled = isSelected
        ) {
            Text(
                text = "$value",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = PrimaryWhite
            )
        }
    }
}

@Composable
private fun BubbleToggle(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = PrimaryWhite
        )
        
        BubbleSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun BubbleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) BrightPurple else EmptyBubblePurple,
        animationSpec = tween(300),
        label = "switch_bg"
    )
    
    val offset by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "switch_offset"
    )
    
    Box(
        modifier = modifier
            .width(70.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor.copy(alpha = 0.3f))
            .clickable { onCheckedChange(!checked) }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .offset(x = (34 * offset).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            backgroundColor,
                            backgroundColor.copy(alpha = 0.8f)
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "danger_button_scale"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        RedAlert.copy(alpha = 0.6f),
                        RedAlert.copy(alpha = 0.8f)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = PrimaryWhite
        )
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = PrimaryWhite
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryGray
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Clear", color = RedAlert)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BrightPurple)
            }
        },
        containerColor = DeepPurple,
        tonalElevation = 8.dp
    )
}

