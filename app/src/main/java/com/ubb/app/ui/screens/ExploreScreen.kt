package com.ubb.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ubb.app.data.TrackRepository
import com.ubb.app.ui.components.*
import com.ubb.app.ui.theme.*

data class FrequencyCategory(
    val name: String,
    val range: String,
    val description: String,
    val colorStart: Color,
    val colorEnd: Color,
)

@Composable
fun ExploreScreen(onTrackClick: (Int) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    val frequencyCategories = listOf(
        FrequencyCategory("Delta", "0.5 - 4 Hz", "Deep Sleep", Color(0xFF1E3A5F), Color(0xFF06B6D4)),
        FrequencyCategory("Theta", "4 - 8 Hz", "Meditation", Color(0xFF4C1D95), Color(0xFF7C3AED)),
        FrequencyCategory("Alpha", "8 - 14 Hz", "Relaxation", Color(0xFF065F46), Color(0xFF10B981)),
        FrequencyCategory("Beta", "14 - 30 Hz", "Focus", Color(0xFF9D174D), Color(0xFFEC4899)),
    )

    val filteredTracks = if (searchQuery.isBlank()) {
        TrackRepository.tracks
    } else {
        TrackRepository.tracks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.waveType.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

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
                text = "Explore",
                style = MaterialTheme.typography.headlineLarge,
                color = OnSurface,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Discover frequencies for every state of mind",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Search Bar
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Search frequencies, moods...", color = TextSecondary, fontSize = 14.sp)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            cursorColor = CyanSecondary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Frequency Grid
        item {
            Text(
                text = "Frequency Bands",
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
                items(frequencyCategories) { cat ->
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(120.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(cat.colorStart, cat.colorEnd),
                                    start = Offset(0f, 0f),
                                    end = Offset(300f, 300f)
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Text(cat.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text(cat.range, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(cat.description, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Curated Section
        item {
            Text(
                text = if (searchQuery.isBlank()) "All Sessions" else "Results",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Track Results
        items(filteredTracks) { track ->
            TrackCard(
                track = track,
                onClick = { onTrackClick(track.id) },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
        }

        if (filteredTracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No sessions found", color = TextSecondary, fontSize = 16.sp)
                }
            }
        }
    }
}
