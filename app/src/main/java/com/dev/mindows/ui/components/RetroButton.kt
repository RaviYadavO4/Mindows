package com.dev.mindows.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.mindows.ui.theme.*

@Composable
fun RetroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(Win95Grey)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .drawBehind {
                val stroke = 2.dp.toPx()
                val topLeftColor = if (isPressed) Win95DarkGrey else Win95White
                val bottomRightColor = if (isPressed) Win95White else Win95DarkGrey

                // Top Line
                drawLine(topLeftColor, Offset(0f, 0f), Offset(size.width, 0f), stroke)
                // Left Line
                drawLine(topLeftColor, Offset(0f, 0f), Offset(0f, size.height), stroke)
                // Bottom Line
                drawLine(bottomRightColor, Offset(0f, size.height), Offset(size.width, size.height), stroke)
                // Right Line
                drawLine(bottomRightColor, Offset(size.width, 0f), Offset(size.width, size.height), stroke)
            }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Win95Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
