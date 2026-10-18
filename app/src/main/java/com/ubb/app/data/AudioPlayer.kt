package com.ubb.app.data

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.ubb.app.data.Track
import com.ubb.app.data.TrackRepository

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentTrack: Track? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val progress: Float = 0f,
)

class AudioPlayer(private val context: Context) {

    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    init {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture?.addListener({
            mediaController = mediaControllerFuture?.get()
            setupController()
        }, ContextCompat.getMainExecutor(context))
    }

    private fun setupController() {
        val controller = mediaController ?: return
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.value = _state.value.copy(isPlaying = isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _state.value = _state.value.copy(duration = controller.duration)
                }
            }
        })
    }

    fun play(track: Track) {
        val controller = mediaController ?: return

        // Get resource ID from the raw resource name
        val resId = context.resources.getIdentifier(
            track.rawResName, "raw", context.packageName
        )

        if (resId == 0) return // Resource not found

        val uri = Uri.parse("android.resource://${context.packageName}/$resId")
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMediaId(track.id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist("Celestial Immersion")
                    .setDisplayTitle(track.title)
                    .build()
            )
            .build()

        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()

        _state.value = _state.value.copy(
            currentTrack = track,
            isPlaying = true,
            currentPosition = 0L,
            progress = 0f
        )
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            controller.play()
        }
    }

    fun seekTo(progress: Float) {
        val controller = mediaController ?: return
        val position = (progress * controller.duration).toLong()
        controller.seekTo(position)
        _state.value = _state.value.copy(
            currentPosition = position,
            progress = progress
        )
    }

    fun updateProgress(): PlayerState {
        val controller = mediaController ?: return _state.value
        val pos = controller.currentPosition
        val dur = controller.duration.coerceAtLeast(1L)
        val prog = pos.toFloat() / dur.toFloat()
        _state.value = _state.value.copy(
            currentPosition = pos,
            duration = dur,
            progress = prog
        )
        return _state.value
    }

    fun skipToNext() {
        val current = _state.value.currentTrack ?: return
        val tracks = TrackRepository.tracks
        val idx = tracks.indexOfFirst { it.id == current.id }
        val next = tracks[(idx + 1) % tracks.size]
        play(next)
    }

    fun skipToPrevious() {
        val current = _state.value.currentTrack ?: return
        val tracks = TrackRepository.tracks
        val idx = tracks.indexOfFirst { it.id == current.id }
        val prev = tracks[(idx - 1 + tracks.size) % tracks.size]
        play(prev)
    }

    fun release() {
        mediaControllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
    }
}
