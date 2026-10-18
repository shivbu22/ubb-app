package com.ubb.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ubb.app.data.TrackRepository
import com.ubb.app.ui.components.*
import com.ubb.app.ui.theme.*

@Composable
fun HomeScreen(onTrackClick: (Int) -> Unit) {
    var selectedCategory by remember { mutableStateOf("All") }
    val filteredTracks = TrackRepository.getByCategory(selectedCategory)
    val featuredTrack = TrackRepository.tracks[4] // Nirvana Meditation Waves

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UBB",
                    color = CyanSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    letterSpacing = 2.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Greeting
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Good Evening,\nExplorer",
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnSurface,
                    lineHeight = 38.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your cosmic journey continues.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Featured Track Card
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceContainerLow)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "DAILY RECOMMENDATION",
                        color = CyanSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = featuredTrack.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "⏱ ${featuredTrack.duration} · ${featuredTrack.waveType}",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(AuroraGradientHorizontal)
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Play Now",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Categories
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(TrackRepository.categories) { category ->
                    CategoryChip(
                        label = category,
                        isSelected = category == selectedCategory,
                        onClick = { selectedCategory = category }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Sessions",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurface
                )
                Text(
                    text = "VIEW ALL",
                    color = CyanSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Track List
        items(filteredTracks) { track ->
            TrackCard(
                track = track,
                onClick = { onTrackClick(track.id) },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
        }
    }
}
