package com.dev.mindows.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RetroVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.width(55.dp).height(30.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val barCount = 5
        val infiniteTransition = rememberInfiniteTransition(label = "Visualizer")

        for (i in 0 until barCount) {
            // Different speed and timing for each instance, so that the animation looks real.
            val duration = remember { (400..800).random() }
            val heightFactor by if (isPlaying) {
                infiniteTransition.animateFloat(
                    initialValue = 0.1f,
                    targetValue = 0.9f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = duration, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "BarHeight"
                )
            } else {
                remember { mutableFloatStateOf(0.1f) }
            }


            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(heightFactor)
                    .background(Color(0xFF003DF5))
            )
        }
    }
}
