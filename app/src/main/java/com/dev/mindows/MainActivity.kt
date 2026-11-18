package com.dev.mindows

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.dev.mindows.ui.screens.explorer.*
import com.dev.mindows.ui.screens.player.*
import com.dev.mindows.ui.components.*
import com.dev.mindows.ui.theme.*
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Mindows Needs Audio Permission to Scan Music", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()

        setContent {
            MindowsTheme {
                val explorerViewModel: ExplorerViewModel = koinViewModel()
                val playerViewModel: PlayerViewModel = koinViewModel()

                LaunchedEffect(Unit) {
                    playerViewModel.initTracking(explorerViewModel)
                }

                val isPlaying by playerViewModel.isPlaying.collectAsState()
                val trackTitle by playerViewModel.trackTitle.collectAsState()
                val artistName by playerViewModel.artistName.collectAsState()
                val currentPos by playerViewModel.currentPosition.collectAsState()
                val duration by playerViewModel.duration.collectAsState()

                val selectedArtist by explorerViewModel.selectedArtist.collectAsState()
                val selectedAlbum by explorerViewModel.selectedAlbum.collectAsState()
                val selectedPlaylistId by explorerViewModel.selectedPlaylistId.collectAsState()

                val isLiked by explorerViewModel.isCurrentTrackLiked.collectAsState()
                val likeText = if (isLiked) "❤️" else "🤍"

                var activeTab by remember { mutableStateOf("My Music") }
                var showFullPlayer by remember { mutableStateOf(false) }

                if (showFullPlayer || selectedArtist != null || selectedAlbum != null || selectedPlaylistId != null) {
                    BackHandler {
                        if (showFullPlayer) {
                            showFullPlayer = false
                        } else if (selectedArtist != null) {
                            explorerViewModel.selectArtist(null)
                        } else if (selectedAlbum != null) {
                            explorerViewModel.selectAlbum(null)
                        } else if (selectedPlaylistId != null) {
                            explorerViewModel.selectPlaylist(null, null)
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxSize().background(WmpBlack)) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        if (showFullPlayer) {
                            WmpPlayerView(
                                viewModel = playerViewModel,
                                explorerViewModel = explorerViewModel,
                                onBack = { showFullPlayer = false }
                            )
                        } else {
                            when (activeTab) {
                                "My Music" -> MyMusicScreen(viewModel = explorerViewModel)
                                "Playlists" -> PlaylistsScreen(viewModel = explorerViewModel)
                                "Artists" -> ArtistsScreen(viewModel = explorerViewModel)
                                "Albums" -> AlbumsScreen(viewModel = explorerViewModel)
                                "Settings" -> SettingsScreen(viewModel = explorerViewModel)
                            }
                        }
                    }

                    if (!showFullPlayer && trackTitle != "No Track Playing" && trackTitle != "Mindows Player") {
                        WmpEmbeddedController(
                            trackTitle = trackTitle,
                            artistName = artistName,
                            isPlaying = isPlaying,
                            currentPosition = currentPos,
                            duration = duration,
                            likeText = likeText,
                            onPlayPause = { playerViewModel.togglePlayPause() },
                            onNext = { playerViewModel.next() },
                            onPrevious = { playerViewModel.previous() },
                            onLikeClick = { playerViewModel.likeCurrentTrack(explorerViewModel) },
                            onSeek = { playerViewModel.seekTo(it) },
                            modifier = Modifier.clickable { showFullPlayer = true }
                        )
                    }

                    if (!showFullPlayer) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalArrangement = Arrangement.Top
                        ) {
                            WmpTabBar(
                                selectedTab = activeTab,
                                onTabSelected = {
                                    activeTab = it
                                    showFullPlayer = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0044D8))
                                    .clickable { if(trackTitle != "No Track Playing" && trackTitle != "Mindows Player") showFullPlayer = true }
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mindows Music Player ®",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "❑ 📋 X", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(permission)
        }
    }
}
