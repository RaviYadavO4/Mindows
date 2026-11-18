package com.dev.mindows.ui.screens.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dev.mindows.R
import com.dev.mindows.ui.theme.*
import com.dev.mindows.ui.components.WmpEmbeddedController
import com.dev.mindows.ui.screens.explorer.ExplorerViewModel

@Composable
fun WmpPlayerView(
    viewModel: PlayerViewModel,
    explorerViewModel: ExplorerViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trackTitle by viewModel.trackTitle.collectAsState()
    val artistName by viewModel.artistName.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPos by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val isLiked by explorerViewModel.isCurrentTrackLiked.collectAsState()

    val tracks by explorerViewModel.tracks.collectAsState()
    val currentTrack = tracks.find { it.title == trackTitle && it.artist == artistName }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WmpBlack)
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 12.dp)
                .border(3.dp, Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center
        ) {
            if (currentTrack != null) {
                val albumArtUri = android.content.ContentUris.withAppendedId(
                    android.net.Uri.parse("content://media/external/audio/albumart"),
                    currentTrack.albumId
                )

                AsyncImage(
                    model = albumArtUri,
                    contentDescription = "Dynamic Album Art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                    error = painterResource(id = R.drawable.ic_album_art),
                    placeholder = painterResource(id = R.drawable.ic_album_art)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_album_art),
                    contentDescription = "Default Album Art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = "◄ BACK",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .background(Color(0x88000000), shape = RoundedCornerShape(4.dp))
                        .clickable { onBack() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        WmpEmbeddedController(
            trackTitle = trackTitle,
            artistName = artistName,
            isPlaying = isPlaying,
            currentPosition = currentPos,
            duration = duration,
            likeText = if (isLiked) "❤️" else "🤍",
            onPlayPause = { viewModel.togglePlayPause() },
            onNext = { viewModel.next() },
            onPrevious = { viewModel.previous() },
            onLikeClick = { viewModel.likeCurrentTrack(explorerViewModel) },
            onSeek = { viewModel.seekTo(it) }
        )
    }
}
