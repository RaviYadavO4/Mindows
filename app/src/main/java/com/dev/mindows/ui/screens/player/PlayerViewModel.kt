package com.dev.mindows.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.dev.mindows.player.playback.MusicController
import com.dev.mindows.ui.screens.explorer.ExplorerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(private val musicController: MusicController) : ViewModel() {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _trackTitle = MutableStateFlow("No Track Playing")
    val trackTitle = _trackTitle.asStateFlow()

    private val _artistName = MutableStateFlow("Unknown Artist")
    val artistName = _artistName.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _currentTrackId = MutableStateFlow<Long?>(null)

    private var playerListener: Player.Listener? = null

    fun initTracking(explorerViewModel: ExplorerViewModel) {
        viewModelScope.launch {
            _currentTrackId.collect { id ->
                explorerViewModel.updateCurrentTrackId(id)
            }
        }
    }

    init {
        observePlayerState()
        startProgressTicker()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            musicController.isConnected.collect { connected ->
                if (connected) {
                    val player = musicController.getController() ?: return@collect
                    updateMetadata(player)

                    playerListener = object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            _isPlaying.value = isPlaying
                        }
                        override fun onMediaMetadataChanged(mediaMetadata: androidx.media3.common.MediaMetadata) {
                            updateMetadata(player)
                        }
                    }.also { player.addListener(it) }
                }
            }
        }
    }

    private fun updateMetadata(player: Player) {
        _trackTitle.value = player.currentMediaItem?.mediaMetadata?.title?.toString() ?: "Mindows Player"
        _artistName.value = player.currentMediaItem?.mediaMetadata?.artist?.toString() ?: "Unknown Artist"
        _duration.value = player.duration.coerceAtLeast(0L)
        _isPlaying.value = player.isPlaying
        val id = player.currentMediaItem?.mediaId?.toLongOrNull()
        _currentTrackId.value = id
    }

    private fun startProgressTicker() {
        viewModelScope.launch {
            while (true) {
                musicController.getController()?.let { player ->
                    if (player.isPlaying) {
                        _currentPosition.value = player.currentPosition
                        _duration.value = player.duration.coerceAtLeast(0L)
                    }
                }
                delay(1000)
            }
        }
    }

    fun likeCurrentTrack(explorerViewModel: ExplorerViewModel) {
        _currentTrackId.value?.let { trackId ->
            explorerViewModel.toggleLikeTrack(trackId)
        }
    }

    fun stopMusic() {
        val player = musicController.getController() ?: return
        player.stop()
        player.seekTo(0L)
    }

    fun seekTo(position: Long) {
        musicController.getController()?.seekTo(position)
    }

    fun togglePlayPause() {
        val player = musicController.getController() ?: return
        if (player.isPlaying) player.pause() else player.play()
    }

    fun next() = musicController.getController()?.seekToNext()
    fun previous() = musicController.getController()?.seekToPrevious()

    override fun onCleared() {
        playerListener?.let { musicController.getController()?.removeListener(it) }
        super.onCleared()
    }
}
