package com.dev.mindows.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.mindows.ui.theme.*

@Composable
fun WmpEmbeddedController(
    trackTitle: String,
    artistName: String,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    likeText: String,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onLikeClick: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(currentPosition, duration) {
        if (!isDragging && duration > 0) {
            sliderPosition = currentPosition.toFloat() / duration.toFloat()
        }
    }
    val minStr = formatTime(if (isDragging && duration > 0) (sliderPosition * duration).toLong() else currentPosition)
    val maxStr = formatTime(duration)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Win95Grey)
            .border(2.dp, Win95White)
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Color.Black)
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artistName.uppercase(),
                    color = WmpNeonGreen,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = trackTitle.uppercase(),
                    color = WmpNeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )
            }
            RetroVisualizer(isPlaying = isPlaying)
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = sliderPosition,
                onValueChange = {
                    isDragging = true
                    sliderPosition = it
                },
                onValueChangeFinished = {
                    isDragging = false
                    if (duration > 0) {
                        onSeek((sliderPosition * duration).toLong())
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Win95DarkGrey,
                    activeTrackColor = Color(0xFF003DF5),
                    inactiveTrackColor = Color.DarkGray
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$minStr/$maxStr",
                color = WmpNeonGreen,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RetroButton(text = "◄◄", onClick = onPrevious, modifier = Modifier.weight(1.0f))
            RetroButton(text = if (isPlaying) "⏸" else "▶", onClick = onPlayPause, modifier = Modifier.weight(1.2f))
            RetroButton(text = likeText, onClick = onLikeClick, modifier = Modifier.weight(0.8f))
            RetroButton(text = "►►", onClick = onNext, modifier = Modifier.weight(1.0f))
        }
    }

}

fun formatTime(ms: Long): String {
    val totalSecs = ms / 1000
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return String.format("%02d:%02d", mins, secs)
}


