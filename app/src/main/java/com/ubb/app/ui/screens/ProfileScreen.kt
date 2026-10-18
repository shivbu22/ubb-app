package com.ubb.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ubb.app.ui.components.AuroraGradientHorizontal
import com.ubb.app.ui.theme.*
import kotlinx.coroutines.launch

import com.ubb.app.data.db.UserRepository

data class Badge(val name: String, val icon: String, val earned: Boolean)
data class SettingsItem(val title: String, val subtitle: String, val icon: ImageVector)

@Composable
fun ProfileScreen(userRepository: UserRepository? = null) {
    val stats by (userRepository?.userStats ?: kotlinx.coroutines.flow.flowOf(null)).collectAsState(initial = null)

    val badges = listOf(
        Badge("Night Owl", "🦉", (stats?.totalSessions ?: 0) > 10),
        Badge("Focus Master", "🎯", (stats?.totalSecondsListened ?: 0L) > 3600),
        Badge("Zen Warrior", "🧘", (stats?.currentStreak ?: 0) > 3),
        Badge("Streak King", "🔥", (stats?.currentStreak ?: 0) > 7),
        Badge("Explorer", "🌍", (stats?.totalSessions ?: 0) > 5),
    )

    val settingsItems = listOf(
        SettingsItem("Audio Quality", "Lossless (FLAC)", Icons.Outlined.Settings),
        SettingsItem("Storage", "42.5 MB used", Icons.Outlined.Storage),
        SettingsItem("Notifications", "Enabled", Icons.Outlined.Notifications),
        SettingsItem("Dark Mode", "Always On", Icons.Outlined.DarkMode),
        SettingsItem("About", "Version 2.0.0", Icons.Outlined.Info),
    )

    val hoursListened = (stats?.totalSecondsListened ?: 0L) / 3600

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineLarge,
                color = OnSurface,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Avatar & Name
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(listOf(VioletPrimary, CyanSecondary)),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Avatar",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Explorer",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnSurface
                )
                Text(
                    text = "Member since April 2026",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("${stats?.totalSessions ?: 0}", "Sessions")
                StatItem("${hoursListened}h", "Listened")
                StatItem("${stats?.currentStreak ?: 0}", "Streak")
                StatItem("${stats?.bestStreak ?: 0}", "Best Streak")
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Badges
        item {
            Text(
                text = "Achievements",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(badges.size) { index ->
                    val badge = badges[index]
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (badge.earned) SurfaceContainerLow else SurfaceContainerLow.copy(alpha = 0.4f)
                            )
                            .then(
                                if (badge.earned) Modifier.border(
                                    1.dp,
                                    VioletPrimary.copy(alpha = 0.3f),
                                    RoundedCornerShape(16.dp)
                                ) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(badge.icon, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                badge.name,
                                color = if (badge.earned) OnSurface else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // UBB Pro Card
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(listOf(VioletPrimary, CyanSecondary)),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(SurfaceContainerLow)
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "UBB Pro",
                            color = PrimaryLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Unlock all frequencies, offline mode, and lossless audio quality.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AuroraGradientHorizontal)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Upgrade to Pro",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Settings
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceContainerLow)
            ) {
                settingsItems.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, color = OnSurface, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                            Text(item.subtitle, color = TextSecondary, fontSize = 12.sp)
                        }
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = OutlineVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = PrimaryLight,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}
