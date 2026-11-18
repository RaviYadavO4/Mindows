package com.dev.mindows.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun RetroWindow(
    title: String,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(Win95Grey)
            .drawBehind {
                val stroke = 2.dp.toPx()
                drawLine(Win95White, Offset(0f, 0f), Offset(size.width, 0f), stroke)
                drawLine(Win95White, Offset(0f, 0f), Offset(0f, size.height), stroke)
                drawLine(Win95DarkGrey, Offset(0f, size.height), Offset(size.width, size.height), stroke)
                drawLine(Win95DarkGrey, Offset(size.width, 0f), Offset(size.width, size.height), stroke)
            }
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Win95Blue)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Win95White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                RetroButton(text = "_", onClick = {})
                RetroButton(text = "×", onClick = onCloseClick)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .background(Win95White)
                .border(1.dp, Win95DarkGrey),
            content = content
        )
    }
}
