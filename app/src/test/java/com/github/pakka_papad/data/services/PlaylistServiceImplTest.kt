package com.github.pakka_papad.data.services

import com.github.pakka_papad.assertCollectionEquals
import com.github.pakka_papad.data.daos.PlaylistDao
import com.github.pakka_papad.data.music.Playlist
import com.github.pakka_papad.data.music.PlaylistExceptId
import com.github.pakka_papad.data.music.PlaylistSongCrossRef
import com.github.pakka_papad.data.music.PlaylistWithSongCount
import com.github.pakka_papad.data.thumbnails.ThumbnailDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlaylistServiceImplTest {

    private val playlistDao = mockk<PlaylistDao>()
    private val thumbnailDao = mockk<ThumbnailDao>()
    private val initialPlaylistsWithSongCount: List<PlaylistWithSongCount>
    private val playlistsFlow: MutableStateFlow<List<PlaylistWithSongCount>>

    init {
        initialPlaylistsWithSongCount = buildList {
            repeat(8) {
                add(
                    PlaylistWithSongCount(
                        playlistId = it.toLong(),
                        playlistName = "Playlist $it",
                        createdAt = 100L + it,
                        count = it,
                        artUri = "art$it"
                    )
                )
            }
        }
        playlistsFlow = MutableStateFlow(initialPlaylistsWithSongCount)
        every { playlistDao.getAllPlaylistWithSongCount() } returns playlistsFlow
    }

    @Test
    fun `verify create playlist with blank input`() = runTest {
        // Given
        val playlistSlot = slot<PlaylistExceptId>()
        coEvery { playlistDao.insertPlaylist(capture(playlistSlot)) } answers {
            val playlist = PlaylistWithSongCount(
                playlistId = initialPlaylistsWithSongCount.size.toLong(),
                playlistName = playlistSlot.captured.playlistName,
                createdAt = playlistSlot.captured.createdAt,
                count = 0,
                artUri = null,
            )
            playlistsFlow.update { it + playlist }
            playlist.playlistId
        }

        val service = PlaylistServiceImpl(
            playlistDao = playlistDao,
            thumbnailDao = thumbnailDao,
        )
        assertCollectionEquals(initialPlaylistsWithSongCount, service.playlists.first())

        // When
        val result = service.createPlaylist("")

        // Then
        val currPlaylists = service.playlists.first()
        assertFalse(result)
        assertCollectionEquals(initialPlaylistsWithSongCount, currPlaylists)
        assertFalse(playlistSlot.isCaptured)
    }

    @Test
    fun `verify create playlist with input having leading and trailing spaces`() = runTest {
        // Given
        val playlistSlot = slot<PlaylistExceptId>()
        coEvery { playlistDao.insertPlaylist(capture(playlistSlot)) } answers {
            val playlist = PlaylistWithSongCount(
                playlistId = initialPlaylistsWithSongCount.size.toLong(),
                playlistName = playlistSlot.captured.playlistName,
                createdAt = playlistSlot.captured.createdAt,
                count = 0,
                artUri = null,
            )
            playlistsFlow.update { it + playlist }
            playlist.playlistId
        }

        val service = PlaylistServiceImpl(
            playlistDao = playlistDao,
            thumbnailDao = thumbnailDao,
        )
        assertCollectionEquals(initialPlaylistsWithSongCount, service.playlists.first())

        // When
        val name = "    New Playlist    "
        val result = service.createPlaylist(name)

        // Then
        val currPlaylists = service.playlists.first()
        assertTrue(result)
        assertTrue(playlistSlot.isCaptured)
        coVerify(exactly = 1) { playlistDao.insertPlaylist(any()) }
        val otherPlaylists = currPlaylists.filter { !initialPlaylistsWithSongCount.contains(it) }
        assertEquals(1, otherPlaylists.size)
    }

    @Test
    fun `verify create playlist with input having no leading or trailing spaces`() = runTest {
        // Given
        val playlistSlot = slot<PlaylistExceptId>()
        coEvery { playlistDao.insertPlaylist(capture(playlistSlot)) } answers {
            val playlist = PlaylistWithSongCount(
                playlistId = initialPlaylistsWithSongCount.size.toLong(),
                playlistName = playlistSlot.captured.playlistName,
                createdAt = playlistSlot.captured.createdAt,
                count = 0,
                artUri = null,
            )
            playlistsFlow.update { it + playlist }
            playlist.playlistId
        }

        val service = PlaylistServiceImpl(
            playlistDao = playlistDao,
            thumbnailDao = thumbnailDao,
        )
        assertCollectionEquals(initialPlaylistsWithSongCount, service.playlists.first())

        // When
        val name = "New Playlist"
        val result = service.createPlaylist(name)

        // Then
        val currPlaylists = service.playlists.first()
        assertTrue(result)
        assertTrue(playlistSlot.isCaptured)
        coVerify(exactly = 1) { playlistDao.insertPlaylist(any()) }
        val otherPlaylists = currPlaylists.filter { !initialPlaylistsWithSongCount.contains(it) }
        assertEquals(1, otherPlaylists.size)
    }

    @Test
    fun `verify delete playlist with valid playlistId`() = runTest {
        // Given
        val playlistId = 1L
        val playlistSlot = slot<Long>()
        coEvery { playlistDao.deletePlaylist(capture(playlistSlot)) } answers {
            playlistsFlow.update { oldList ->
                oldList.filter { it.playlistId != playlistSlot.captured }
            }
        }
        coEvery { playlistDao.getPlaylist(playlistId) } answers {
            val playlistWithSongCount = playlistsFlow.value.firstOrNull {
                it.playlistId == firstArg<Long>()
            }
            if (playlistWithSongCount == null) null
            else Playlist(
                playlistId = playlistWithSongCount.playlistId,
                playlistName = playlistWithSongCount.playlistName,
                createdAt = playlistWithSongCount.createdAt,
                artUri = playlistWithSongCount.artUri,
            )
        }
        val thumbLoc = slot<String>()
        coEvery { thumbnailDao.markDelete(capture(thumbLoc)) } returns Unit

        val service = PlaylistServiceImpl(
            playlistDao = playlistDao,
            thumbnailDao = thumbnailDao,
        )
        assertCollectionEquals(initialPlaylistsWithSongCount, service.playlists.first())

        // When
        service.deletePlaylist(playlistId)

        // Then
        val currPlaylists = service.playlists.first()
        coVerify(exactly = 1) { playlistDao.deletePlaylist(any()) }
        val removedPlaylists = initialPlaylistsWithSongCount.filter { !currPlaylists.contains(it) }
        assertEquals(1, removedPlaylists.size)
        assertEquals(playlistId, removedPlaylists[0].playlistId)
        assertEquals(thumbLoc.captured, "art$playlistId")
    }

    @Test
    fun `verify delete playlist with invalid playlistId`() = runTest {
        // Given
        val playlistId = 31L
        val playlistSlot = slot<Long>()
        coEvery { playlistDao.deletePlaylist(capture(playlistSlot)) } answers {
            playlistsFlow.update { oldList ->
                oldList.filter { it.playlistId != playlistSlot.captured }
            }
        }
        coEvery { playlistDao.getPlaylist(playlistId) } answers {
            val playlistWithSongCount = playlistsFlow.value.firstOrNull {
                it.playlistId == firstArg<Long>()
            }
            if (playlistWithSongCount == null) null
            else Playlist(
                playlistId = playlistWithSongCount.playlistId,
                playlistName = playlistWithSongCount.playlistName,
                createdAt = playlistWithSongCount.createdAt,
                artUri = playlistWithSongCount.artUri,
            )
        }

        val service = PlaylistServiceImpl(
            playlistDao = playlistDao,
            thumbnailDao = thumbnailDao,
        )
        assertCollectionEquals(initialPlaylistsWithSongCount, service.playlists.first())

        // When
        service.deletePlaylist(playlistId)

        // Then
        val currPlaylists = service.playlists.first()
        coVerify(exactly = 1) { playlistDao.deletePlaylist(any()) }
        val removedPlaylists = initialPlaylistsWithSongCount.filter { !currPlaylists.contains(it) }
        assertEquals(0, removedPlaylists.size)
        assertCollectionEquals(initialPlaylistsWithSongCount, currPlaylists)
    }

    @Test
    fun `verify addSongToPlaylist with valid playlistId`() = runTest {
        // Given
        val songLocations = buildList {
            repeat(4){ add("/storage/emulated/0/folder0/song$it.mp3") }
        }
        val playlistId = 1L
        val refs = slot<List<PlaylistSongCrossRef>>()
        coEvery { playlistDao.insertPlaylistSongCrossRef(capture(refs)) } answers {
            refs.captured.forEach {  ref ->
                val currList = playlistsFlow.value
                val playlist = currList
                    .firstOrNull { it.playlistId == ref.playlistId }
                    ?: return@forEach
                val updatedPlaylist = playlist.copy(
                    count = playlist.count + 1
                )
                val newList = currList.filter { it != playlist } + updatedPlaylist
                playlistsFlow.update { newList }
            }
        }

        val service = PlaylistServiceImpl(
            playlistDao = playlistDao,
            thumbnailDao = thumbnailDao,
        )
        assertCollectionEquals(initialPlaylistsWithSongCount, service.playlists.first())

        // When
        service.addSongsToPlaylist(songLocations, playlistId)

        // Then
        val currPlaylists = service.playlists.first()
        coVerify(exactly = 1) { playlistDao.insertPlaylistSongCrossRef(any()) }
        val (iPNotChanged, iPChanged) = initialPlaylistsWithSongCount
            .partition { it.playlistId != playlistId }
        val (cPNotChanged, cPChanged) = currPlaylists
            .partition { it.playlistId != playlistId }
        assertCollectionEquals(iPNotChanged, cPNotChanged)
        assertEquals(1, iPChanged.size)
        assertEquals(1, cPChanged.size)
        assertEquals(iPChanged[0].playlistId, cPChanged[0].playlistId)
        assertEquals(iPChanged[0].count + songLocations.size, cPChanged[0].count)
    }

    @Test
    fun `verify addSongToPlaylist with invalid playlistId`() = runTest {
        // Given
        val songLocations = buildList {
            repeat(4){ add("/storage/emulated/0/folder0/song$it.mp3") }
        }
        val playlistId = 31L
        val refs = slot<List<PlaylistSongCrossRef>>()
        coEvery { playlistDao.insertPlaylistSongCrossRef(capture(refs)) } answers {
            refs.captured.forEach {  ref ->
                val currList = playlistsFlow.value
                val playlist = currList
                    .firstOrNull { it.playlistId == ref.playlistId }
                    ?: return@forEach
                val updatedPlaylist = playlist.copy(
                    count = playlist.count + 1
                )
                val newList = currList.filter { it != playlist } + updatedPlaylist
                playlistsFlow.update { newList }
            }
        }

        val service = PlaylistServiceImpl(
            playlistDao = playlistDao,
            thumbnailDao = thumbnailDao,
        )
        assertCollectionEquals(initialPlaylistsWithSongCount, service.playlists.first())

        // When
        service.addSongsToPlaylist(songLocations, playlistId)

        // Then
        val currPlaylists = service.playlists.first()
        coVerify(exactly = 1) { playlistDao.insertPlaylistSongCrossRef(any()) }
        assertCollectionEquals(initialPlaylistsWithSongCount, currPlaylists)
    }

    @Test
    fun `verify removeSongFromPlaylist with valid playlistId`() = runTest {
        // Given
        val songLocations = buildList {
            repeat(2) { add("/storage/emulated/0/song$it.mp3") }
        }
        val playlistId = 3L
        val slots = mutableListOf<PlaylistSongCrossRef>()
        coEvery { playlistDao.deletePlaylistSongCrossRef(capture(slots)) } answers {
            val arg = firstArg<PlaylistSongCrossRef>()
            val playlist = playlistsFlow.value
                .firstOrNull { it.playlistId == arg.playlistId }
            playlist?.let {  p ->
                val updatedPlaylist = p.copy(
                    count = p.count - 1
                )
                playlistsFlow.update { oldList ->
                    oldList.filter { it.playlistId != arg.playlistId } + updatedPlaylist
                }
            }
        }

        val service = PlaylistServiceImpl(
            playlistDao = playlistDao,
            thumbnailDao = thumbnailDao,
        )
        assertCollectionEquals(initialPlaylistsWithSongCount, service.playlists.first())

        // When
        service.removeSongsFromPlaylist(songLocations, playlistId)

        // Then
        val currPlaylists = service.playlists.first()
        coVerify(exactly = songLocations.size) { playlistDao.deletePlaylistSongCrossRef(any()) }
        val (iPNotChanged, iPChanged) = initialPlaylistsWithSongCount
            .partition { it.playlistId != playlistId }
        val (cPNotChanged, cPChanged) = currPlaylists
            .partition { it.playlistId != playlistId }
        assertCollectionEquals(iPNotChanged, cPNotChanged)
        assertEquals(1, iPChanged.size)
        assertEquals(1, cPChanged.size)
        assertEquals(iPChanged[0].playlistId, cPChanged[0].playlistId)
        assertEquals(iPChanged[0].count - songLocations.size, cPChanged[0].count)
    }

    @Test
    fun `verify removeSongFromPlaylist with invalid playlistId`() = runTest {
        // Given
        val songLocations = buildList {
            repeat(2) { add("/storage/emulated/0/song$it.mp3") }
        }
        val playlistId = 31L
        val slots = mutableListOf<PlaylistSongCrossRef>()
        coEvery { playlistDao.deletePlaylistSongCrossRef(capture(slots)) } answers {
            val arg = firstArg<PlaylistSongCrossRef>()
            val playlist = playlistsFlow.value
                .firstOrNull { it.playlistId == arg.playlistId }
            playlist?.let {  p ->
                val updatedPlaylist = p.copy(
                    count = p.count - 1
                )
                playlistsFlow.update { oldList ->
                    oldList.filter { it.playlistId != arg.playlistId } + updatedPlaylist
                }
            }
        }

        val service = PlaylistServiceImpl(
            playlistDao = playlistDao,
            thumbnailDao = thumbnailDao,
        )
        assertCollectionEquals(initialPlaylistsWithSongCount, service.playlists.first())

        // When
        service.removeSongsFromPlaylist(songLocations, playlistId)

        // Then
        val currPlaylists = service.playlists.first()
        coVerify(exactly = songLocations.size) { playlistDao.deletePlaylistSongCrossRef(any()) }
        assertCollectionEquals(initialPlaylistsWithSongCount, currPlaylists)
    }
}