package com.dev.mindows.ui.screens.explorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.dev.mindows.data.local.database.PlaylistDao
import com.dev.mindows.data.local.database.PlaylistEntity
import com.dev.mindows.data.local.database.PlaylistSongCrossRef
import com.dev.mindows.data.model.AudioTrack
import com.dev.mindows.data.repository.MusicRepository
import com.dev.mindows.player.playback.MusicController
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExplorerViewModel(
    private val repository: MusicRepository,
    private val musicController: MusicController,
    private val playlistDao: PlaylistDao
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<AudioTrack>>(emptyList())
    val tracks = _tracks.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _desktopColor = MutableStateFlow(0xFF008080)
    val desktopColor = _desktopColor.asStateFlow()


    private val _selectedArtist = MutableStateFlow<String?>(null)
    val selectedArtist = _selectedArtist.asStateFlow()

    private val _selectedAlbum = MutableStateFlow<String?>(null)
    val selectedAlbum = _selectedAlbum.asStateFlow()

    private val _selectedPlaylistId = MutableStateFlow<Long?>(null)
    val selectedPlaylistId = _selectedPlaylistId.asStateFlow()

    private val _selectedPlaylistName = MutableStateFlow<String?>(null)
    val selectedPlaylistName = _selectedPlaylistName.asStateFlow()

    val dbPlaylists: StateFlow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTrackId = MutableStateFlow<Long?>(null)

    val isCurrentTrackLiked: StateFlow<Boolean> = _currentTrackId
        .flatMapLatest { trackId ->
            if (trackId == null) flowOf(false)
            else playlistDao.getSongIdsForPlaylist(1).map { ids -> ids.contains(trackId) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun loadSongs() {
        viewModelScope.launch {
            _isLoading.value = true
            _tracks.value = repository.getLocalTracks()
            _isLoading.value = false
        }
    }

    fun updateCurrentTrackId(trackId: Long?) {
        _currentTrackId.value = trackId
    }

    fun selectArtist(artistName: String?) {
        _selectedArtist.value = artistName
    }

    fun selectAlbum(albumName: String?) {
        _selectedAlbum.value = albumName
    }

    fun selectPlaylist(playlistId: Long?, playlistName: String?) {
        _selectedPlaylistId.value = playlistId
        _selectedPlaylistName.value = playlistName
    }

    fun getTracksForArtist(artistName: String): List<AudioTrack> {
        return _tracks.value.filter { it.artist == artistName }
    }

    fun getTracksForAlbum(albumName: String): List<AudioTrack> {
        return _tracks.value.filter { it.album == albumName }
    }

    fun getTracksForPlaylist(audioTrackIds: List<Long>): List<AudioTrack> {
        return _tracks.value.filter { audioTrackIds.contains(it.id) }
    }

    fun getSongIdsForPlaylist(playlistId: Long): Flow<List<Long>> {
        return playlistDao.getSongIdsForPlaylist(playlistId)
    }

    fun createNewPlaylist(name: String, emoji: String = "📁") {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                playlistDao.createPlaylist(PlaylistEntity(name = name, iconEmoji = emoji))
            }
        }
    }

    fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            playlistDao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, trackId))
        }
    }

    fun toggleLikeTrack(trackId: Long) {
        viewModelScope.launch {
            val likedIds = playlistDao.getSongIdsForPlaylist(1).first()
            if (likedIds.contains(trackId)) {
                playlistDao.removeSongFromPlaylist(1, trackId)
            } else {
                playlistDao.addSongToPlaylist(PlaylistSongCrossRef(1, trackId))
            }
        }
    }

    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            playlistDao.deletePlaylist(playlist)
        }
    }

    fun playAllAndStart(selectedTrack: AudioTrack, allTracks: List<AudioTrack>) {
        val controller = musicController.getController() ?: return
        controller.clearMediaItems()

        val mediaItems = allTracks.map { track ->
            MediaItem.Builder()
                .setMediaId(track.id.toString())
                .setUri(track.uri)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .setAlbumTitle(track.album)
                        .build()
                ).build()
        }

        controller.setMediaItems(mediaItems)

        val targetIndex = allTracks.indexOf(selectedTrack)
        if (targetIndex != -1) {
            controller.seekTo(targetIndex, 0L)
        }

        controller.prepare()
        controller.play()
    }
}
