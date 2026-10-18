package com.ubb.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ubb.app.ui.components.AuroraGradient
import com.ubb.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun TimerScreen() {
    var selectedMinutes by remember { mutableIntStateOf(30) }
    var isRunning by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(selectedMinutes * 60) }
    var fadeOutEnabled by remember { mutableStateOf(true) }
    var stopAfterSession by remember { mutableStateOf(true) }

    val quickSelects = listOf(15, 30, 45, 60, 90)

    // Timer countdown
    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
        if (remainingSeconds == 0) isRunning = false
    }

    val progress = if (selectedMinutes > 0) {
        remainingSeconds.toFloat() / (selectedMinutes * 60).toFloat()
    } else 0f

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeDisplay = "%02d:%02d".format(minutes, seconds)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sleep Timer",
                style = MaterialTheme.typography.headlineLarge,
                color = OnSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Set a timer to auto-stop playback",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
        }

        // Circular Timer
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(260.dp)
                    .drawBehind {
                        val strokeWidth = 12.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val topLeft = Offset(
                            (size.width - radius * 2) / 2,
                            (size.height - radius * 2) / 2
                        )

                        // Background circle
                        drawArc(
                            color = SurfaceContainerHigh,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Progress arc
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(VioletPrimary, CyanSecondary, VioletPrimary)
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeDisplay,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "remaining",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Play/Pause Button
        item {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AuroraGradient)
                    .clickable {
                        if (!isRunning && remainingSeconds == 0) {
                            remainingSeconds = selectedMinutes * 60
                        }
                        isRunning = !isRunning
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isRunning) "Pause Timer" else "Start Timer",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Quick Select
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                quickSelects.forEach { mins ->
                    val isSelected = mins == selectedMinutes && !isRunning
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isSelected) VioletPrimary else SurfaceContainerHigh)
                            .clickable {
                                if (!isRunning) {
                                    selectedMinutes = mins
                                    remainingSeconds = mins * 60
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "${mins}m",
                            color = if (isSelected) Color.White else OnSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
        }

        // Settings Toggles
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceContainerLow)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Fade Out Audio", color = OnSurface, fontWeight = FontWeight.Medium)
                        Text("Gradually reduce volume", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = fadeOutEnabled,
                        onCheckedChange = { fadeOutEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = VioletPrimary,
                            uncheckedTrackColor = SurfaceContainerHigh
                        )
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Stop After Session", color = OnSurface, fontWeight = FontWeight.Medium)
                        Text("Automatically stop playback", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = stopAfterSession,
                        onCheckedChange = { stopAfterSession = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = VioletPrimary,
                            uncheckedTrackColor = SurfaceContainerHigh
                        )
                    )
                }
            }
        }
    }
}
