package com.dev.mindows.ui.screens.explorer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dev.mindows.data.local.database.PlaylistEntity
import com.dev.mindows.ui.components.RetroButton
import com.dev.mindows.ui.components.RetroWindow
import com.dev.mindows.ui.theme.*

@Composable
fun PlaylistsScreen(
    viewModel: ExplorerViewModel,
    modifier: Modifier = Modifier
) {
    val dbPlaylists by viewModel.dbPlaylists.collectAsState()
    val selectedPlaylistId by viewModel.selectedPlaylistId.collectAsState()
    val selectedPlaylistName by viewModel.selectedPlaylistName.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var playlistToDelete by remember { mutableStateOf<PlaylistEntity?>(null) }
    var newPlaylistName by remember { mutableStateOf("") }

    val playlistSongIds by if (selectedPlaylistId != null) {
        viewModel.getSongIdsForPlaylist(selectedPlaylistId!!).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<Long>()) }
    }

    Column(modifier = modifier.fillMaxSize().background(WmpBlack).padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedPlaylistId != null) {
                Text(
                    text = "◄ BACK", color = WmpNeonGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable { viewModel.selectPlaylist(null, null) }
                )
            } else {
                RetroButton(text = "[+] NEW PLAYLIST", onClick = { showCreateDialog = true })
            }
            Text(
                text = selectedPlaylistName?.uppercase() ?: "MY PLAYLISTS",
                color = WmpNeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(40.dp))
        }

        Box(modifier = Modifier.fillMaxSize().background(WmpDarkGrey).border(1.dp, Win95DarkGrey)) {
            if (selectedPlaylistId != null) {
                val playlistTracks = viewModel.getTracksForPlaylist(playlistSongIds)
                if (playlistTracks.isEmpty()) {
                    Text(text = "This playlist is empty.", color = Color.Gray, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(playlistTracks) { idx, track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.playAllAndStart(track, playlistTracks) }
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
                                val minutes = (track.duration / 1000) / 60
                                val seconds = (track.duration / 1000) % 60
                                Text(text = String.format("%02d:%02d", minutes, seconds), fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = WmpNeonGreen)
                            }
                        }
                    }
                }
            } else {
                if (dbPlaylists.isEmpty()) {
                    Text(text = "No Playlists Found.", color = Color.Gray, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(dbPlaylists) { index, playlist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = { viewModel.selectPlaylist(playlist.id, playlist.name) },
                                        onLongClick = {
                                            if (playlist.id > 5) {
                                                playlistToDelete = playlist
                                                showDeleteDialog = true
                                            }
                                        }
                                    )
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${index + 1}", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(20.dp))
                                Box(modifier = Modifier.size(32.dp).background(Color.White, shape = RoundedCornerShape(4.dp)).border(1.dp, Win95DarkGrey, shape = RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                                    Text(text = playlist.iconEmoji, fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(text = playlist.name.uppercase(), color = WmpNeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    Text(text = if (playlist.id <= 5) "System Folder" else "Hold to delete", color = Color.Gray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        Dialog(onDismissRequest = { showCreateDialog = false }) {
            RetroWindow(title = "New_Playlist.exe", modifier = Modifier.fillMaxWidth().wrapContentHeight(), onCloseClick = { showCreateDialog = false }) {
                Column(modifier = Modifier.background(Win95Grey).padding(12.dp)) {
                    Text(text = "Enter Playlist Name:", color = Win95Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 6.dp))
                    BasicTextField(value = newPlaylistName, onValueChange = { newPlaylistName = it }, modifier = Modifier.fillMaxWidth().background(Color.White).border(1.dp, Win95DarkGrey).padding(6.dp), singleLine = true)
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        RetroButton(text = "Cancel", onClick = { showCreateDialog = false; newPlaylistName = "" })
                        Spacer(modifier = Modifier.width(6.dp))
                        RetroButton(text = "OK", onClick = { if (newPlaylistName.isNotBlank()) { viewModel.createNewPlaylist(newPlaylistName); newPlaylistName = ""; showCreateDialog = false } })
                    }
                }
            }
        }
    }

    if (showDeleteDialog && playlistToDelete != null) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            RetroWindow(title = "Confirm_Delete.sys", modifier = Modifier.fillMaxWidth().wrapContentHeight(), onCloseClick = { showDeleteDialog = false }) {
                Column(modifier = Modifier.background(Win95Grey).padding(12.dp)) {
                    Text(
                        text = "Are you sure you want to delete the playlist '${playlistToDelete!!.name.uppercase()}'?",
                        color = Win95Black, fontSize = 12.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        RetroButton(text = "No", onClick = { showDeleteDialog = false; playlistToDelete = null })
                        Spacer(modifier = Modifier.width(6.dp))
                        RetroButton(text = "Yes", onClick = {
                            viewModel.deletePlaylist(playlistToDelete!!)
                            showDeleteDialog = false
                            playlistToDelete = null
                        })
                    }
                }
            }
        }
    }
}