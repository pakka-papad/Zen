package com.github.pakka_papad.search

import com.github.pakka_papad.MainDispatcherRule
import com.github.pakka_papad.data.music.Album
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.data.services.PlayerService
import com.github.pakka_papad.data.services.QueueService
import com.github.pakka_papad.data.services.SearchService
import com.github.pakka_papad.util.MessageStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val messageStore = mockk<MessageStore>(relaxed = true)
    private val playerService = mockk<PlayerService>()
    private val queueService = mockk<QueueService>()
    private val searchService = mockk<SearchService>()

    private val query = "Good"
    private lateinit var songs: List<Song>
    private lateinit var albums: List<Album>
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        songs = buildList {
            repeat(5) {
                val mockSong = mockk<Song>()
                every { mockSong.title } returns "Good song $it"
                add(mockSong)
            }
        }
        albums = buildList {
            repeat(4) {
                add(Album("Good album $it"))
            }
        }
        coEvery { searchService.searchSongs(query) } returns songs
        coEvery { searchService.searchAlbums(query) } returns albums
        coEvery { playerService.startServiceIfNotRunning(any(), any()) } answers {
            queueService.setQueue(firstArg(), secondArg())
        }
        viewModel = SearchViewModel(
            messageStore = messageStore,
            playerService = playerService,
            queueService = queueService,
            searchService = searchService,
            crashReporter = mockk(relaxed = true)
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.startCollection() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.searchResult.collect()
        }
    }

    @Test
    fun `verify updateQuery`() = runTest {
        // Given
        startCollection()

        // When
        viewModel.updateQuery(query)

        // Then
        assertEquals(query, viewModel.query.value)
        assertEquals(songs, viewModel.searchResult.value.songs)
        assertEquals(0, viewModel.searchResult.value.albums.size)
    }

    @Test
    fun `verify updateType`() = runTest {
        // Given
        startCollection()
        viewModel.updateQuery(query)

        // When
        viewModel.updateType(SearchType.Albums)

        // Then
        assertEquals(SearchType.Albums, viewModel.searchType.value)
        assertEquals(albums, viewModel.searchResult.value.albums)
        assertEquals(0, viewModel.searchResult.value.songs.size)
    }

    @Test
    fun `verify setQueue`() = runTest {
        // Given
        startCollection()
        every { queueService.setQueue(songs, 0) } returns Unit

        // When
        viewModel.setQueue(songs, 0)

        // Then
        coVerify(exactly = 1) {
            queueService.setQueue(songs, 0)
            playerService.startServiceIfNotRunning(songs, 0)
        }
        coVerifyOrder {
            playerService.startServiceIfNotRunning(songs, 0)
            queueService.setQueue(songs, 0)
        }
    }
}