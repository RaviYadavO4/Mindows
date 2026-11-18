package com.dev.mindows.ui.screens.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.dev.mindows.data.model.ArtistGroup
import com.dev.mindows.data.model.AudioTrack
import com.dev.mindows.ui.components.RetroButton
import com.dev.mindows.ui.components.RetroWindow
import com.dev.mindows.ui.theme.*



@Composable
fun ArtistsScreen(
    viewModel: ExplorerViewModel,
    modifier: Modifier = Modifier
) {
    val tracks by viewModel.tracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedArtist by viewModel.selectedArtist.collectAsState()
    val dbPlaylists by viewModel.dbPlaylists.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTrackToSave by remember { mutableStateOf<AudioTrack?>(null) }

    val artistGroups = tracks.groupBy { it.artist }.entries.mapIndexed { index, entry ->
        val dummyValues = listOf("10.00", "27.30", "15.45", "08.20", "19.15")
        ArtistGroup(
            name = entry.key,
            trackCount = entry.value.size,
            displayValue = dummyValues[index % dummyValues.size]
        )
    }

    Column(modifier = modifier.fillMaxSize().background(WmpBlack).padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedArtist != null) {
                Text(
                    text = "◄ BACK",
                    color = WmpNeonGreen,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable { viewModel.selectArtist(null) }
                )
            } else {
                Spacer(modifier = Modifier.width(40.dp))
            }
            Text(
                text = selectedArtist?.uppercase() ?: "ARTISTS",
                color = WmpNeonGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(40.dp))
        }

        Box(modifier = Modifier.fillMaxSize().background(WmpDarkGrey).border(1.dp, Win95DarkGrey)) {
            if (isLoading) {
                CircularProgressIndicator(color = WmpNeonGreen, modifier = Modifier.align(Alignment.Center))
            } else if (selectedArtist != null) {
                val artistTracks = viewModel.getTracksForArtist(selectedArtist!!)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(artistTracks) { idx, track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.playAllAndStart(track, artistTracks) }
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
                            Text(text = String.format("%02d:%02d", minutes, seconds), fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = WmpNeonGreen)
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(artistGroups) { index, artist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectArtist(artist.name) }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${index + 1}", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(20.dp))
                            Box(modifier = Modifier.size(28.dp).background(Color(0xFF2A2A2A)).border(1.dp, Color.DarkGray), contentAlignment = Alignment.Center) {
                                Text(text = "👤", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(artist.name, color = WmpTextWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${artist.trackCount} Songs", color = WmpNeonGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                            Text(artist.displayValue, color = WmpNeonGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
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
