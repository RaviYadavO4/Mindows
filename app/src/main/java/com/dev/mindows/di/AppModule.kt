package com.dev.mindows.di

import androidx.room.Room
import com.dev.mindows.data.local.MediaStoreScanner
import com.dev.mindows.data.local.database.PlaylistDatabase
import com.dev.mindows.data.repository.MusicRepository
import com.dev.mindows.data.repository.MusicRepositoryImpl
import com.dev.mindows.player.playback.MusicController
import com.dev.mindows.ui.screens.explorer.ExplorerViewModel
import com.dev.mindows.ui.screens.player.PlayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            PlaylistDatabase::class.java,
            "mindows_music_db"
        ).build()
    }

    single { get<PlaylistDatabase>().playlistDao() }

    single { MediaStoreScanner(get()) }

    single<MusicRepository> { MusicRepositoryImpl(get()) }

    single { MusicController(get()) }

    viewModel { ExplorerViewModel(get(), get(), get()) }
    viewModel { PlayerViewModel(get()) }
}
