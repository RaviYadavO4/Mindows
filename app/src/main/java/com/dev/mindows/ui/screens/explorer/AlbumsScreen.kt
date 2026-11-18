package com.dev.mindows.ui.screens.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.dev.mindows.R
import com.dev.mindows.data.model.AlbumGroup
import com.dev.mindows.data.model.AudioTrack
import com.dev.mindows.ui.components.RetroButton
import com.dev.mindows.ui.components.RetroWindow
import com.dev.mindows.ui.theme.*
import kotlinx.coroutines.launch




@Composable
fun AlbumsScreen(
    viewModel: ExplorerViewModel,
    modifier: Modifier = Modifier
) {
    val tracks by viewModel.tracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedAlbum by viewModel.selectedAlbum.collectAsState()
    val dbPlaylists by viewModel.dbPlaylists.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTrackToSave by remember { mutableStateOf<AudioTrack?>(null) }

    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadSongs()
    }

    val albumGroups = remember(tracks) {
        tracks.groupBy { it.album }.entries.mapIndexed { index, entry ->
            val retroDiskColors = listOf(Color(0xFF5A6275), Color(0xFF333333), Color(0xFF4A3E56), Color(0xFF3E5647))
            val firstTrackInAlbum = entry.value.firstOrNull()
            AlbumGroup(
                title = entry.key,
                trackCount = entry.value.size,
                albumId = firstTrackInAlbum?.albumId ?: 0L,
                diskColor = retroDiskColors[index % retroDiskColors.size]
            )
        }.sortedBy { it.title.trim().uppercase() }
    }

    val availableAlphabets = remember(albumGroups) {
        albumGroups.map { album ->
            val firstChar = album.title.trim().firstOrNull() ?: return@map ""
            if (firstChar.isDigit()) "0-9" else firstChar.uppercase().toString()
        }
            .filter { it.isNotBlank() }
            .distinct()
            .sortedWith { o1, o2 ->
                if (o1 == "0-9") -1 else if (o2 == "0-9") 1 else o1.compareTo(o2)
            }
    }

    val alphabetToGridIndexMap = remember(albumGroups) {
        val map = mutableMapOf<String, Int>()
        albumGroups.forEachIndexed { index, album ->
            val firstChar = album.title.trim().firstOrNull() ?: return@forEachIndexed
            val key = if (firstChar.isDigit()) "0-9" else firstChar.uppercase().toString()
            if (!map.containsKey(key)) {
                map[key] = index
            }
        }
        map
    }

    val activeAlphabet = remember(gridState.firstVisibleItemIndex, albumGroups) {
        if (albumGroups.isNotEmpty() && gridState.firstVisibleItemIndex < albumGroups.size) {
            val firstChar = albumGroups[gridState.firstVisibleItemIndex].title.trim().firstOrNull() ?: ' '
            if (firstChar.isDigit()) "0-9" else firstChar.uppercase().toString()
        } else {
            ""
        }
    }

    Row(modifier = modifier.fillMaxSize().background(WmpBlack).padding(8.dp)) {
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (selectedAlbum != null) {
                    Text("◄ BACK", color = WmpNeonGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.clickable { viewModel.selectAlbum(null) })
                } else {
                    Spacer(modifier = Modifier.width(40.dp))
                }
                Text(text = selectedAlbum?.uppercase() ?: "ALBUMS", color = WmpNeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.width(40.dp))
            }

            Box(modifier = Modifier.fillMaxSize().background(WmpDarkGrey).border(1.dp, Win95DarkGrey).padding(8.dp)) {
                if (isLoading) {
                    CircularProgressIndicator(color = WmpNeonGreen, modifier = Modifier.align(Alignment.Center))
                } else if (selectedAlbum != null) {
                    val albumTracks = viewModel.getTracksForAlbum(selectedAlbum!!)
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(albumTracks) { idx, track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.playAllAndStart(track, albumTracks) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .drawBehind {
                                        drawLine(Color(0xFF1A1A1A), Offset(0f, size.height), Offset(size.width, size.height), 0.5.dp.toPx())
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💽", fontSize = 16.sp, modifier = Modifier.padding(end = 12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = track.title, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = WmpTextWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(text = track.artist, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                RetroButton(text = "+", onClick = { selectedTrackToSave = track; showAddDialog = true }, modifier = Modifier.padding(horizontal = 6.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                val minutes = (track.duration / 1000) / 60
                                val seconds = (track.duration / 1000) % 60
                                Text(text = String.format("%02d:%02d", minutes, seconds), fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = WmpNeonGreen)
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(albumGroups) { _, album ->
                            Column(modifier = Modifier.fillMaxWidth().clickable { viewModel.selectAlbum(album.title) }, horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.size(90.dp).clip(CircleShape).background(album.diskColor).border(2.dp, Color.Gray, CircleShape), contentAlignment = Alignment.Center) {
                                    if (album.albumId != 0L) {
                                        val albumArtUri = android.content.ContentUris.withAppendedId(
                                            android.net.Uri.parse("content://media/external/audio/albumart"),
                                            album.albumId
                                        )
                                        AsyncImage(model = albumArtUri, contentDescription = "CD Album Art", modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop, error = painterResource(id = R.drawable.ic_album_art))
                                    }
                                    Box(modifier = Modifier.size(54.dp).border(1.dp, Color(0x22FFFFFF), CircleShape))
                                    Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(WmpDarkGrey).border(1.dp, Color.LightGray, CircleShape))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(album.title.uppercase(), color = WmpNeonGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${album.trackCount} Tracks", color = Color.Gray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
        if (selectedAlbum == null && availableAlphabets.isNotEmpty()) {
            Spacer(modifier = Modifier.width(6.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .width(24.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                availableAlphabets.forEach { letter ->
                    val isCurrentActive = activeAlphabet == letter
                    Text(
                        text = letter,
                        color = if (isCurrentActive) WmpNeonGreen else Color.Gray,
                        fontSize = if (letter == "0-9") 8.sp else 10.sp,
                        fontWeight = if (isCurrentActive) FontWeight.ExtraBold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                alphabetToGridIndexMap[letter]?.let { targetGridIndex ->
                                    coroutineScope.launch {
                                        gridState.animateScrollToItem(targetGridIndex)
                                    }
                                }
                            }
                            .padding(vertical = 1.dp)
                    )
                }
            }
        }
    }
    if (showAddDialog && selectedTrackToSave != null) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            RetroWindow(title = "Add_To_Playlist.dll", modifier = Modifier.fillMaxWidth().wrapContentHeight(), onCloseClick = { showAddDialog = false }) {
                Column(modifier = Modifier.background(Win95Grey).padding(12.dp)) {
                    Text(text = "Select Playlist for:\n${selectedTrackToSave!!.title.uppercase()}", color = Win95Black, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.White).border(1.dp, Win95DarkGrey)) {
                        if (dbPlaylists.isEmpty()) {
                            Text(text = "No playlists found.", color = Color.Gray, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.align(Alignment.Center))
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                itemsIndexed(dbPlaylists) { _, playlist ->
                                    Row(modifier = Modifier.fillMaxWidth().clickable { viewModel.addTrackToPlaylist(playlist.id, selectedTrackToSave!!.id); showAddDialog = false; selectedTrackToSave = null }.padding(10.dp, 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = playlist.iconEmoji, modifier = Modifier.padding(end = 8.dp))
                                        Text(text = playlist.name.uppercase(), color = Win95Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        RetroButton(text = "Close", onClick = { showAddDialog = false; selectedTrackToSave = null })
                    }
                }
            }
        }
    }
}