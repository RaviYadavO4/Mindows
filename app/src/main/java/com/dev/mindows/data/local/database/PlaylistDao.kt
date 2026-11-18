package com.dev.mindows.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPlaylist(playlist: PlaylistEntity): Long

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND audioTrackId = :audioTrackId")
    suspend fun removeSongFromPlaylist(playlistId: Long, audioTrackId: Long)

    @Query("SELECT audioTrackId FROM playlist_songs WHERE playlistId = :playlistId")
    fun getSongIdsForPlaylist(playlistId: Long): Flow<List<Long>>
}
