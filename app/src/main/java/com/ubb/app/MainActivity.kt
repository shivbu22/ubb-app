package com.ubb.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ubb.app.data.AudioPlayer
import com.ubb.app.ui.components.AuroraGradient
import com.ubb.app.ui.screens.*
import com.ubb.app.ui.theme.*
import com.ubb.app.data.db.AppDatabase
import com.ubb.app.data.db.UserRepository
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private var audioPlayer: AudioPlayer? = null
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(applicationContext)
        userRepository = UserRepository(database.userStatsDao())
        audioPlayer = AudioPlayer(applicationContext)
        enableEdgeToEdge()
        setContent {
            UBBTheme {
                UBBApp(audioPlayer!!, userRepository)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer?.release()
    }
}

sealed class Screen {
    data object Splash : Screen()
    data object Home : Screen()
    data object Explore : Screen()
    data object Timer : Screen()
    data object Profile : Screen()
    data class Player(val trackId: Int) : Screen()
}

data class NavItem(val label: String, val icon: ImageVector, val screen: Screen)

@Composable
fun UBBApp(audioPlayer: AudioPlayer, userRepository: UserRepository) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var selectedNavIndex by remember { mutableIntStateOf(0) }

    val playerState by audioPlayer.state.collectAsState()

    var lastPlayStartTime by remember { mutableLongStateOf(0L) }

    // Progress ticker & Session Recording
    LaunchedEffect(playerState.isPlaying) {
        if (playerState.isPlaying) {
            android.util.Log.d("SessionTest", "Started playing, setting startTime")
            lastPlayStartTime = System.currentTimeMillis()
            while (true) {
                audioPlayer.updateProgress()
                delay(500L)
            }
        } else {
            android.util.Log.d("SessionTest", "Stopped playing, lastStartTime=$lastPlayStartTime")
            if (lastPlayStartTime > 0) {
                val durationMs = System.currentTimeMillis() - lastPlayStartTime
                val durationSec = durationMs / 1000
                android.util.Log.d("SessionTest", "Duration ms=$durationMs, sec=$durationSec")
                if (durationSec >= 2) { // Only record if listened for at least 2 seconds
                    try {
                        userRepository.recordSession(durationSec)
                        android.util.Log.d("SessionTest", "Session recorded successfully!")
                    } catch (e: Exception) {
                        android.util.Log.e("SessionTest", "Error recording session", e)
                    }
                }
                lastPlayStartTime = 0L
            }
        }
    }

    val navItems = listOf(
        NavItem("Home", Icons.Filled.Home, Screen.Home),
        NavItem("Explore", Icons.Filled.Explore, Screen.Explore),
        NavItem("Timer", Icons.Filled.Timer, Screen.Timer),
        NavItem("Profile", Icons.Filled.Person, Screen.Profile),
    )

    val showBottomNav = currentScreen !is Screen.Splash && currentScreen !is Screen.Player

    Box(modifier = Modifier.fillMaxSize()) {
        // Screen content
        when (val screen = currentScreen) {
            is Screen.Splash -> {
                SplashScreen(onGetStarted = {
                    currentScreen = Screen.Home
                    selectedNavIndex = 0
                })
            }
            is Screen.Home -> {
                HomeScreen(onTrackClick = { trackId ->
                    audioPlayer.play(
                        com.ubb.app.data.TrackRepository.getById(trackId) ?: return@HomeScreen
                    )
                    currentScreen = Screen.Player(trackId)
                })
            }
            is Screen.Explore -> {
                ExploreScreen(onTrackClick = { trackId ->
                    audioPlayer.play(
                        com.ubb.app.data.TrackRepository.getById(trackId) ?: return@ExploreScreen
                    )
                    currentScreen = Screen.Player(trackId)
                })
            }
            is Screen.Timer -> {
                TimerScreen()
            }
            is Screen.Profile -> {
                ProfileScreen(userRepository)
            }
            is Screen.Player -> {
                PlayerScreen(
                    trackId = screen.trackId,
                    audioPlayer = audioPlayer,
                    onBack = {
                        currentScreen = navItems[selectedNavIndex].screen
                    }
                )
            }
        }

        // Bottom Navigation Bar
        if (showBottomNav) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurfaceContainerHigh.copy(alpha = 0.95f))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEachIndexed { index, item ->
                        val isSelected = index == selectedNavIndex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    selectedNavIndex = index
                                    currentScreen = item.screen
                                }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AuroraGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(9.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.label,
                                color = if (isSelected) PrimaryLight else TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
