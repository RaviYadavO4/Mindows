package com.dev.mindows.data.repository

import com.dev.mindows.data.local.MediaStoreScanner
import com.dev.mindows.data.model.AudioTrack

class MusicRepositoryImpl(
    private val scanner: MediaStoreScanner
) : MusicRepository {

    override suspend fun getLocalTracks(): List<AudioTrack> {
        return scanner.fetchLocalAudio()
    }
}
