package com.dev.mindows.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.mindows.ui.theme.Win95Grey

@Composable
fun WmpTabBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("My Music", "Playlists", "Artists", "Albums", "Settings")
    val windowsActiveBlue = Color(0xFF0044D8)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Win95Grey)
            .padding(top = 0.dp, bottom = 0.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == selectedTab
            val animatedHeight by animateDpAsState(
                targetValue = if (isSelected) 30.dp else 24.dp,
                animationSpec = tween(durationMillis = 250),
                label = "TabHeight"
            )
            val animatedBgColor by animateColorAsState(
                targetValue = if (isSelected) windowsActiveBlue else Color.Transparent,
                animationSpec = tween(durationMillis = 200),
                label = "TabBgColor"
            )
            val animatedTextColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.Black,
                animationSpec = tween(durationMillis = 200),
                label = "TabTextColor"
            )

            Box(
                modifier = Modifier
                    .height(animatedHeight)
                    .background(animatedBgColor)
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab,
                    color = animatedTextColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
