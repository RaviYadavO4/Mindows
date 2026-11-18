package com.dev.mindows.data.model

import androidx.compose.ui.graphics.Color

data class AlbumGroup(
    val title: String,
    val trackCount: Int,
    val albumId: Long,
    val diskColor: Color
)