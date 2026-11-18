package com.dev.mindows.ui.screens.explorer

import com.dev.mindows.data.local.database.PlaylistDao
import com.dev.mindows.data.repository.MusicRepository
import com.dev.mindows.player.playback.MusicController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class ExplorerViewModelTest {

    // Test dispatcher for synchronizing thread scheduling in a test environment
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ExplorerViewModel
    private val repository: MusicRepository = mock(MusicRepository::class.java)
    private val musicController: MusicController = mock(MusicController::class.java)
    private val playlistDao: PlaylistDao = mock(PlaylistDao::class.java)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Switch the core thread to the test dispatcher
        viewModel = ExplorerViewModel(repository, musicController, playlistDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Restore threads to normal
    }

    @Test
    fun selectArtist_updatesSelectedArtistStateCorrectly() = runTest {
        // Given
        val artistName = "Tyler, The Creator"

        // When
        viewModel.selectArtist(artistName)

        // Then - Ensure that the same artist name is reflected in the live state.
        assertEquals(artistName, viewModel.selectedArtist.value)
    }

    @Test
    fun selectAlbum_updatesSelectedAlbumStateCorrectly() = runTest {
        // Given
        val albumName = "Nostalgia95 Special Edition"

        // When
        viewModel.selectAlbum(albumName)

        // Then
        assertEquals(albumName, viewModel.selectedAlbum.value)
    }
}
