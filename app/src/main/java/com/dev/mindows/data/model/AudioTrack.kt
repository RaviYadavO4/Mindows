package com.dev.mindows.data.model

import android.net.Uri

data class AudioTrack(
    val id: Long,
    val uri: Uri,
    val title: String,
    val artist: String,
    val duration: Long,
    val album: String,
    val size: Long,
    val albumId: Long
)
