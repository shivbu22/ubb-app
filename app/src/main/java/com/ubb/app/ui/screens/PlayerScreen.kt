package com.ubb.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ubb.app.data.AudioPlayer
import com.ubb.app.data.TrackRepository
import com.ubb.app.ui.components.*
import com.ubb.app.ui.theme.*

@Composable
fun PlayerScreen(trackId: Int, audioPlayer: AudioPlayer, onBack: () -> Unit) {
    val track = TrackRepository.getById(trackId) ?: return
    val playerState by audioPlayer.state.collectAsState()

    val currentPos = playerState.currentPosition / 1000
    val totalDur = playerState.duration / 1000
    val posMin = (currentPos / 60).toInt()
    val posSec = (currentPos % 60).toInt()
    val durMin = (totalDur / 60).toInt()
    val durSec = (totalDur % 60).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceContainerLowest)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = OnSurfaceVariant,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBack() }
            )
            Text(
                text = "NOW PLAYING",
                color = OnSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Share",
                tint = OnSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Album Art with Pulsing Glow
        PulsingGlowCircle {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = track.frequency, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text(text = track.waveType, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Track Info
        Text(
            text = track.title,
            style = MaterialTheme.typography.headlineMedium,
            color = OnSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${track.waveType} · ${track.frequency} · ${track.category}",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Progress Bar
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = playerState.progress,
                onValueChange = { audioPlayer.seekTo(it) },
                colors = SliderDefaults.colors(
                    thumbColor = CyanSecondary,
                    activeTrackColor = VioletPrimary,
                    inactiveTrackColor = SurfaceContainerHigh
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("%d:%02d".format(posMin, posSec), color = TextSecondary, fontSize = 12.sp)
                Text("%d:%02d".format(durMin, durSec), color = TextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Playback Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Shuffle, "Shuffle", tint = OnSurfaceVariant, modifier = Modifier.size(24.dp))
            Icon(
                Icons.Filled.SkipPrevious, "Previous", tint = OnSurface,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { audioPlayer.skipToPrevious() }
            )

            // Play/Pause
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AuroraGradient)
                    .clickable { audioPlayer.togglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (playerState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Icon(
                Icons.Filled.SkipNext, "Next", tint = OnSurface,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { audioPlayer.skipToNext() }
            )
            Icon(Icons.Filled.Repeat, "Repeat", tint = OnSurfaceVariant, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(Icons.Filled.FavoriteBorder, "Favorite", tint = OnSurfaceVariant, modifier = Modifier.size(24.dp))
            Icon(Icons.AutoMirrored.Filled.PlaylistAdd, "Add to Playlist", tint = OnSurfaceVariant, modifier = Modifier.size(24.dp))
            Icon(Icons.AutoMirrored.Filled.VolumeUp, "Volume", tint = OnSurfaceVariant, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Binaural Info Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainer)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Binaural Beat Info",
                    color = PrimaryLight,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = track.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
