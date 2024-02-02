package com.github.pakka_papad.collection

import com.github.pakka_papad.MainDispatcherRule
import com.github.pakka_papad.components.SortOptions
import com.github.pakka_papad.data.music.Artist
import com.github.pakka_papad.data.music.ArtistWithSongs
import com.github.pakka_papad.data.music.Playlist
import com.github.pakka_papad.data.music.PlaylistWithSongs
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.data.services.PlayerService
import com.github.pakka_papad.data.services.PlaylistService
import com.github.pakka_papad.data.services.QueueService
import com.github.pakka_papad.data.services.SongService
import com.github.pakka_papad.util.MessageStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CollectionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val messageStore: MessageStore = mockk()
    private val playlistService: PlaylistService = mockk(relaxed = true)
    private val songService: SongService = mockk(relaxed = true)
    private val queueService: QueueService = mockk(relaxed = true)
    private val playerService: PlayerService = mockk()
    private lateinit var viewModel: CollectionViewModel

    private val artistName = "Some artist"
    private lateinit var artistWithSongs: ArtistWithSongs
    private val mockMessage = "Sample message"
    private val queueList = mutableListOf<Song>()
    private val currentSongFlow = MutableStateFlow<Song?>(null)

    @Before
    fun setup() {
        artistWithSongs = ArtistWithSongs(
            artist = Artist(artistName),
            songs = buildList {
                repeat(5) {
                    val mockSong = mockk<Song>(relaxed = true)
                    every { mockSong.location } returns "/storage/emulated/0/song$it.mp3"
                    every { mockSong.title } returns "song$it"
                    add(mockSong)
                }
                shuffle()
            }
        )
        every { songService.getArtistWithSongsByName(artistName) } returns flowOf(artistWithSongs)
        every { queueService.currentSong } returns currentSongFlow
        every { queueService.queue } returns queueList
        every { messageStore.getString(any()) } returns mockMessage
        every { messageStore.getString(allAny()) } returns mockMessage
        coEvery { playerService.startServiceIfNotRunning(any(), any()) } answers {
            queueService.setQueue(firstArg(), secondArg())
        }
        viewModel = CollectionViewModel(
            messageStore = messageStore,
            playlistService = playlistService,
            songService = songService,
            playerService = playerService,
            queueService = queueService,
            crashReporter = mockk(relaxed = true)
        )
        assertEquals("", viewModel.message.value)
        assertNull(viewModel.collectionUi.value)
        assertEquals(viewModel.chosenSortOrder.value, SortOptions.Default.ordinal)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.load() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.loadCollection(CollectionType(CollectionType.ArtistType, artistName))
            viewModel.collectionUi.collect()
        }
    }

    @Test
    fun `verify correct loading`() = runTest {
        // Given
        load()

        // When

        // Then
        assertEquals(artistWithSongs.songs, viewModel.collectionUi.value?.songs)
    }

    @Test
    fun `verify sort order update`() = runTest {
        // Given
        load()

        // When
        val sortOrder = SortOptions.TitleASC.ordinal
        viewModel.updateSortOrder(sortOrder)

        // Then
        assertEquals(sortOrder, viewModel.chosenSortOrder.value)
        assertEquals(
            artistWithSongs.songs.sortedBy { it.title },
            viewModel.collectionUi.value?.songs
        )
    }

    @Test
    fun `verify setQueue`() = runTest {
        // Given
        load()

        // When
        viewModel.setQueue(artistWithSongs.songs, 0)

        // Then
        coVerify(exactly = 1) {
            queueService.setQueue(artistWithSongs.songs, 0)
            playerService.startServiceIfNotRunning(artistWithSongs.songs, 0)
        }
        coVerifyOrder {
            playerService.startServiceIfNotRunning(artistWithSongs.songs, 0)
            queueService.setQueue(artistWithSongs.songs, 0)
        }
    }

    @Test
    fun `given empty queue verify addToQueue song`() = runTest {
        // Given
        load()

        // When
        val mockSong = mockk<Song>()
        every { mockSong.location } returns "/storage/emulated/0/song0.mp3"
        viewModel.addToQueue(mockSong)

        // Then
        coVerify(exactly = 1) {
            playerService.startServiceIfNotRunning(listOf(mockSong), 0)
            queueService.setQueue(listOf(mockSong), 0)
        }
        coVerifyOrder {
            playerService.startServiceIfNotRunning(listOf(mockSong), 0)
            queueService.setQueue(listOf(mockSong), 0)
        }
    }

    private fun fillQueue(): List<Song> {
        val songs = buildList {
            repeat(7) {
                val mockSong = mockk<Song>()
                every { mockSong.location } returns "/storage/emulated/0/song$it.mp3"
                add(mockSong)
            }
        }
        queueList.addAll(songs)
        queueService.setQueue(songs, 1)
        currentSongFlow.update { songs[0] }
        return songs
    }

    @Test
    fun `given non-empty queue verify addToQueue song`() = runTest {
        // Given
        load()
        val songs = fillQueue()

        // When
        val mockSong = mockk<Song>()
        every { mockSong.location } returns "/storage/emulated/0/new-song.mp3"
        viewModel.addToQueue(mockSong)

        // Then
        verify(exactly = 1) {
            queueService.append(mockSong)
        }
    }

    @Test
    fun `given empty queue verify addToQueue songs`() = runTest {
        // Given
        load()

        // When
        val mockSongs = buildList {
            repeat(3) {
                val mockSong = mockk<Song>()
                every { mockSong.location } returns "/storage/emulated/0/new-song$it.mp3"
                add(mockSong)
            }
        }
        viewModel.addToQueue(mockSongs)

        // Then
        coVerify(exactly = 1) {
            queueService.setQueue(mockSongs, 0)
            playerService.startServiceIfNotRunning(mockSongs, 0)
        }
        coVerifyOrder {
            playerService.startServiceIfNotRunning(mockSongs, 0)
            queueService.setQueue(mockSongs, 0)
        }
    }

    @Test
    fun `given non-empty queue verify addToQueue songs`() = runTest {
        // Given
        load()
        val songs = fillQueue()

        // When
        val mockSongs = buildList {
            repeat(3) {
                val mockSong = mockk<Song>()
                every { mockSong.location } returns "/storage/emulated/0/new-song$it.mp3"
                add(mockSong)
            }
        }
        viewModel.addToQueue(mockSongs)

        // Then
        verify(exactly = 1) {
            queueService.append(mockSongs)
        }
    }

    @Test
    fun `verify change favourite`() = runTest {
        // Given
        load()

        // When
        val location = "/storage/emulated/0/song9.mp3"
        val mockSong = Song(
            location = location,
            title = "", size = "", addedDate = "", modifiedDate = "", artist = "",
            albumArtist = "", composer = "", lyricist = "", genre = "", year = 0,
            durationMillis = 0L, durationFormatted = "", bitrate = 0f, sampleRate = 0f)
        viewModel.changeFavouriteValue(mockSong)

        // Then
        val slot1 = slot<Song>()
        val slot2 = slot<Song>()
        coVerify(exactly = 1) {
            queueService.update(capture(slot1))
            songService.updateSong(capture(slot2))
        }
        assertEquals(location, slot1.captured.location)
        assertEquals(location, slot2.captured.location)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verify remove from playlist`() = runTest {
        // Given
        val playlistId = 3445L
        every { playlistService.getPlaylistWithSongsById(playlistId) } returns flowOf(
            PlaylistWithSongs(
                playlist = Playlist(
                    playlistId = playlistId,
                    playlistName = "Playlist-0",
                    createdAt = 0L
                ),
                songs = artistWithSongs.songs
            )
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.loadCollection(CollectionType(CollectionType.PlaylistType, playlistId.toString()))
            viewModel.collectionUi.collect()
        }

        // When
        val mockSong = mockk<Song>(relaxed = true)
        every { mockSong.location } returns "/storage/emulated/0/song0.mp3"
        viewModel.removeFromPlaylist(mockSong)

        // Then
        val expectedList = listOf(mockSong.location)
        coVerify(exactly = 1) {
            playlistService.removeSongsFromPlaylist(expectedList, playlistId)
        }
    }
}