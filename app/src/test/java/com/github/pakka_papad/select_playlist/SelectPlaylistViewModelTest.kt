package com.github.pakka_papad.select_playlist

import com.github.pakka_papad.MainDispatcherRule
import com.github.pakka_papad.data.music.BlacklistedSong
import com.github.pakka_papad.data.music.PlaylistWithSongCount
import com.github.pakka_papad.data.services.BlacklistService
import com.github.pakka_papad.data.services.PlaylistService
import com.github.pakka_papad.util.MessageStore
import com.github.pakka_papad.util.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertIs

class SelectPlaylistViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val messageStore = mockk<MessageStore>(relaxed = true)
    private val blacklistService = mockk<BlacklistService>()
    private val playlistService = mockk<PlaylistService>()

    private lateinit var playlists: List<PlaylistWithSongCount>
    private lateinit var blacklistedSongs: List<BlacklistedSong>

    private lateinit var viewModel: SelectPlaylistViewModel

    @Before
    fun setup() {
        playlists = buildList {
            repeat(5) {
                add(
                    PlaylistWithSongCount(
                        playlistId = it.toLong(),
                        playlistName = "Playlist $it",
                        createdAt = 0L,
                        count = 1,
                    )
                )
            }
        }
        every { playlistService.playlists } returns MutableStateFlow(playlists)
        blacklistedSongs = buildList {
            repeat(10) {
                add(
                    BlacklistedSong(
                        location = "/storage/emulated/0/song$it.mp3",
                        title = "Song$it",
                        artist = ""
                    )
                )
            }
        }
        every { blacklistService.blacklistedSongs } returns MutableStateFlow(blacklistedSongs)
        viewModel = SelectPlaylistViewModel(
            messageStore = messageStore,
            blacklistService = blacklistService,
            playlistService = playlistService,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.startCollection() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.playlistsWithSongCount.collect()
        }
    }

    @Test
    fun `verify toggle at index when insertState is Idle`() = runTest {
        // Given
        startCollection()
        assertEquals(viewModel.selectList.size, playlists.size)
        viewModel.selectList.forEach { assertFalse(it) }

        // When
        val index = playlists.size-1
        viewModel.toggleSelectAtIndex(index)

        // Then
        assertTrue(viewModel.selectList[index])
    }

    @Test
    fun `verify toggle at index when insertState is Idle and index is invalid`() = runTest {
        // Given
        startCollection()
        assertEquals(viewModel.selectList.size, playlists.size)
        viewModel.selectList.forEach { assertFalse(it) }

        // When
        val index = playlists.size
        viewModel.toggleSelectAtIndex(index)

        // Then
        viewModel.selectList.forEach { assertFalse(it) }
    }

    @Test
    fun `verify toggle at index when insertState is no Idle`() = runTest {
        // Given
        startCollection()
        assertEquals(viewModel.selectList.size, playlists.size)
        viewModel.selectList.forEach { assertFalse(it) }
        viewModel._insertState.update { Resource.Loading() }

        // When
        val index = playlists.size-1
        viewModel.toggleSelectAtIndex(index)

        // Then
        viewModel.selectList.forEach { assertFalse(it) }
    }

    @Test
    fun `verify add songs to playlists when insertState is Idle`() = runTest {
        // Given
        startCollection()
        viewModel.toggleSelectAtIndex(0)
        viewModel.toggleSelectAtIndex(1)

        // When
        val whiteListedSongLoc = "/storage/emulated/0/other-song0.mp3"
        val songLocations = blacklistedSongs.take(2).map { it.location } + listOf(whiteListedSongLoc)
        coEvery { playlistService.addSongsToPlaylist(listOf(whiteListedSongLoc),0) } returns Unit
        coEvery { playlistService.addSongsToPlaylist(listOf(whiteListedSongLoc),1) } returns Unit
        viewModel.addSongsToPlaylists(songLocations.toTypedArray())

        // Then
        coVerify(exactly = 1) {
            playlistService.addSongsToPlaylist(listOf(whiteListedSongLoc), 0)
            playlistService.addSongsToPlaylist(listOf(whiteListedSongLoc), 1)
        }
        assertIs<Resource.Success<String>>(viewModel.insertState.value)
    }

    @Test
    fun `verify add songs to playlists when insertState is not Idle`() = runTest {
        // Given
        startCollection()
        viewModel.toggleSelectAtIndex(0)
        viewModel.toggleSelectAtIndex(1)
        viewModel._insertState.update { Resource.Loading() }

        // When
        val whiteListedSongLoc = "/storage/emulated/0/other-song0.mp3"
        val songLocations = blacklistedSongs.take(2).map { it.location } + listOf(whiteListedSongLoc)
        coEvery { playlistService.addSongsToPlaylist(listOf(whiteListedSongLoc),0) } returns Unit
        coEvery { playlistService.addSongsToPlaylist(listOf(whiteListedSongLoc),1) } returns Unit
        viewModel.addSongsToPlaylists(songLocations.toTypedArray())

        // Then
        coVerify(exactly = 0) {
            playlistService.addSongsToPlaylist(any(), any())
        }
    }
}