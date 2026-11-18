package com.dev.mindows.ui.screens.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dev.mindows.data.model.AudioTrack
import com.dev.mindows.ui.components.RetroButton
import com.dev.mindows.ui.components.RetroWindow
import com.dev.mindows.ui.theme.*

@Composable
fun MyMusicScreen(
    viewModel: ExplorerViewModel,
    modifier: Modifier = Modifier
) {
    val tracks by viewModel.tracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val dbPlaylists by viewModel.dbPlaylists.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTrackToSave by remember { mutableStateOf<AudioTrack?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadSongs()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WmpBlack)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "=== ALL TRACKS ===",
                color = WmpNeonGreen,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WmpDarkGrey)
                .border(1.dp, Win95DarkGrey)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = WmpNeonGreen, modifier = Modifier.align(Alignment.Center))
            } else if (tracks.isEmpty()) {
                Text(
                    text = "No Tracks Found on Device.",
                    fontFamily = FontFamily.Monospace,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(tracks) { track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.playAllAndStart(track, tracks) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .drawBehind {
                                    val strokeWidth = 0.5.dp.toPx()
                                    drawLine(
                                        color = Color(0xFF1A1A1A),
                                        start = Offset(0f, size.height),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = strokeWidth
                                    )
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "💽", fontSize = 16.sp, modifier = Modifier.padding(end = 12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = WmpTextWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = track.artist,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            RetroButton(
                                text = "+",
                                onClick = {
                                    selectedTrackToSave = track
                                    showAddDialog = true
                                },
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            val minutes = (track.duration / 1000) / 60
                            val seconds = (track.duration / 1000) % 60
                            Text(
                                text = String.format("%02d:%02d", minutes, seconds),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = WmpNeonGreen
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog && selectedTrackToSave != null) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            RetroWindow(
                title = "Add_To_Playlist.dll",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                onCloseClick = { showAddDialog = false }
            ) {
                Column(
                    modifier = Modifier
                        .background(Win95Grey)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Select Playlist for:\n${selectedTrackToSave!!.title.uppercase()}",
                        color = Win95Black,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color.White)
                            .border(1.dp, Win95DarkGrey)
                    ) {
                        if (dbPlaylists.isEmpty()) {
                            Text(
                                text = "No playlists found.\nCreate one in 'Playlists' tab first.",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(dbPlaylists) { playlist ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.addTrackToPlaylist(playlist.id, selectedTrackToSave!!.id)
                                                showAddDialog = false
                                                selectedTrackToSave = null
                                            }
                                            .padding(10.dp, 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = playlist.iconEmoji, modifier = Modifier.padding(end = 8.dp))
                                        Text(
                                            text = playlist.name.uppercase(),
                                            color = Win95Black,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RetroButton(
                            text = "Close",
                            onClick = {
                                showAddDialog = false
                                selectedTrackToSave = null
                            }
                        )
                    }
                }
            }
        }
    }
}