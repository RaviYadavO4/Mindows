package com.dev.mindows.data.repository

import com.dev.mindows.data.model.AudioTrack

interface MusicRepository {
    suspend fun getLocalTracks(): List<AudioTrack>
}
