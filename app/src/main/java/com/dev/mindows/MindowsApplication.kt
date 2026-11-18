package com.dev.mindows

import android.app.Application
import com.dev.mindows.data.local.database.PlaylistDao
import com.dev.mindows.data.local.database.PlaylistEntity
import com.dev.mindows.di.appModule
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MindowsApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MindowsApplication)
            modules(appModule)
        }
        val dao: PlaylistDao = get()
        MainScope().launch {
            val currentPlaylists = dao.getAllPlaylists().first()
            if (currentPlaylists.isEmpty()) {
                dao.createPlaylist(PlaylistEntity(id = 1, name = "LIKED SONGS", iconEmoji = "❤️"))
                dao.createPlaylist(PlaylistEntity(id = 2, name = "NOSTALGIA95", iconEmoji = "🌐"))
                dao.createPlaylist(PlaylistEntity(id = 3, name = "MY TOP TEN", iconEmoji = "🎬"))
                dao.createPlaylist(PlaylistEntity(id = 4, name = "PLAYLIST #5", iconEmoji = "❓"))
                dao.createPlaylist(PlaylistEntity(id = 5, name = "IT'S SUMMER TIME, BABY!", iconEmoji = "☀️"))
            }
        }
    }
}
