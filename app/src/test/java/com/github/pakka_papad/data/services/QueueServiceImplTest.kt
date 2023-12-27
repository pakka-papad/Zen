package com.github.pakka_papad.data.services

import com.github.pakka_papad.assertCollectionEquals
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.nowplaying.RepeatMode
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class QueueServiceImplTest {

    private val service = QueueServiceImpl()
    private val callback = mockk<QueueService.Listener>(relaxed = true)

    @Before
    fun setup(){
        service.addListener(callback)
    }

    @After
    fun close(){
        assertCollectionEquals(service.locations, service.queue.map { it.location })
    }

    @Test
    fun `given empty queue verify append song`() = runTest {
        // Given
        assertEquals(emptyList(), service.queue)

        // When
        val mockSongLocation = "storage/emulated/0/song0.mp3"
        val mockkSong = mockk<Song>()
        every { mockkSong.location } returns mockSongLocation
        val result = service.append(mockkSong)

        // Then
        assertTrue(result)
        assertEquals(1, service.queue.size)
        assertEquals(mockSongLocation, service.queue[0].location)
        assertEquals(1, service.locations.size)
        assertContains(service.locations, mockSongLocation)
        verify(exactly = 1) {
            callback.onAppend(mockkSong)
        }
        verify(exactly = 0) {
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `given empty queue verify append songs`() = runTest {
        // Given
        assertEquals(emptyList(), service.queue)

        // When
        val mockSongLocations = mutableListOf<String>()
        val mockkSongs = buildList {
            repeat(5) {
                val loc = "storage/emulated/0/song$it.mp3"
                val mockSong = mockk<Song>()
                every { mockSong.location } returns loc
                mockSongLocations.add(loc)
                add(mockSong)
            }
        }
        val result = service.append(mockkSongs)

        // Then
        assertTrue(result)
        assertEquals(mockkSongs, service.queue)
        verify(exactly = 1) {
            callback.onAppend(mockkSongs)
        }
        verify(exactly = 0) {
            callback.onAppend(capture(slot<Song>()))
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    private fun setupQueue(){
        repeat(6){
            val mockSong = mockk<Song>(relaxed = true)
            every { mockSong.location } returns "/storage/emulated/0/mock-song$it.mp3"
            service.mutableQueue.add(mockSong)
            service.locations.add(mockSong.location)
        }
    }

    @Test
    fun `given non-empty queue verify append song`() = runTest {
        // Given
        setupQueue()
        val initialQueue = service.queue
        val initialLocations = service.locations.toList()

        // When
        val mockSongLocation = "/storage/emulated/0/song0.mp3"
        val mockSong = mockk<Song>()
        every { mockSong.location } returns mockSongLocation
        service.append(mockSong)

        // Then
        assertEquals(initialQueue + mockSong, service.queue)
        assertCollectionEquals(initialLocations + mockSongLocation, service.locations)
        verify(exactly = 1) { callback.onAppend(mockSong) }
        verify(exactly = 0) {
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `given non-empty queue verify append songs`() = runTest {
        // Given
        setupQueue()
        val initialQueue = service.queue
        val initialLocations = service.locations.toList()

        // When
        val mockSongLocations = mutableListOf<String>()
        val mockSongs = buildList {
            repeat(5) {
                val mockSong = mockk<Song>()
                every { mockSong.location } returns "/storage/emulated/0/song$it.mp3"
                mockSongLocations.add(mockSong.location)
                add(mockSong)
            }
        }
        service.append(mockSongs)

        // Then
        assertEquals(initialQueue + mockSongs, service.queue)
        assertCollectionEquals(initialLocations + mockSongLocations, service.locations)
        verify(exactly = 1) { callback.onAppend(mockSongs) }
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `verify update song when song is not in queue`() = runTest {
        // Given
        setupQueue()
        val initialQueue = service.queue
        val initialLocations = service.locations.toList()

        // When
        val mockSongLocation = "storage/emulated/0/song0.mp3"
        val mockkSong = mockk<Song>()
        every { mockkSong.location } returns mockSongLocation
        val result = service.update(mockkSong)

        // Then
        assertFalse(result)
        assertEquals(initialQueue, service.queue)
        assertCollectionEquals(initialLocations, service.locations)
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `verify update song when song is in queue`() = runTest {
        // Given
        setupQueue()
        val iQueue = service.queue
        val initialLocations = service.locations.toList()

        // When
        val song = mockk<Song>()
        every { song.location } returns iQueue[0].location
        every { song.favourite } returns true
        val result = service.update(song)

        // Then
        assertTrue(result)
        assertEquals(
            listOf(song) + iQueue.slice(1 until iQueue.size),
            service.queue
        )
        assertCollectionEquals(initialLocations, service.locations)
        verify(exactly = 1) { callback.onUpdate(song, any()) }
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `verify update song when song is in queue and currently playing`() = runTest {
        // Given
        setupQueue()
        val iQueue = service.queue
        service._currentSong.update { service.queue[0] }
        val initialLocations = service.locations.toList()

        // When
        val song = mockk<Song>()
        every { song.location } returns iQueue[0].location
        every { song.favourite } returns true
        val result = service.update(song)

        // Then
        assertTrue(result)
        assertEquals(
            listOf(song) + iQueue.slice(1 until iQueue.size),
            service.queue
        )
        assertCollectionEquals(initialLocations, service.locations)
        verify(exactly = 1) { callback.onUpdate(song, 0) }
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `verify moveSong with invalid indices`() = runTest {
        // Given
        setupQueue()
        val s = service.queue.size
        val initialLocations = service.locations.toList()

        // When
        val result = service.moveSong(1,s)

        // Then
        assertFalse(result)
        assertCollectionEquals(initialLocations, service.locations)
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `verify moveSong with valid indices`() = runTest {
        // Given
        setupQueue()
        val s = service.queue.size
        val initialLocations = service.locations.toList()

        // When
        val result = service.moveSong(1,s-1)

        // Then
        assertTrue(result)
        assertCollectionEquals(initialLocations, service.locations)
        verify(exactly = 1) { callback.onMove(1, s-1) }
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `verify clearQueue`() = runTest {
        // Given
        setupQueue()

        // When
        service.clearQueue()

        // Then
        verify(exactly = 1) { callback.onClear() }
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onSetQueue(any(), any())
        }
        assertEquals(0, service.locations.size)
    }

    @Test
    fun `verify setQueue`() = runTest {
        // Given
        setupQueue()

        // When
        val newQueue = buildList {
            repeat(5) {
                val mockSong = mockk<Song>()
                every { mockSong.location } returns "/storage/emulated/0/song$it.mp3"
                add(mockSong)
            }
        }
        service.setQueue(newQueue, 1)

        // Then
        assertEquals(newQueue, service.queue)
        assertCollectionEquals(service.locations, newQueue.map { it.location })
        verify(exactly = 1) { callback.onSetQueue(newQueue, 1) }
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
        }
    }

    @Test
    fun `verify getSongAtIndex with valid index`() = runTest {
        // Given
        setupQueue()

        // When
        val result = service.getSongAtIndex(service.queue.size-1)

        // Then
        assertNotNull(result)
        assertContains(service.queue, result)
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `verify getSongAtIndex with invalid index`() = runTest {
        // Given
        setupQueue()

        // When
        val result = service.getSongAtIndex(service.queue.size)

        // Then
        assertNull(result)
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `verify setCurrentSong with valid index`() = runTest {
        // Given
        setupQueue()

        // When
        val index = service.queue.size-1
        val song = service.queue[index]
        service.setCurrentSong(index)

        // Then
        assertEquals(service.currentSong.value?.location, song.location)
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `verify setCurrentSong with invalid index`() = runTest {
        // Given
        setupQueue()

        // When
        val index = service.queue.size
        service.setCurrentSong(index)

        // Then
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }

    @Test
    fun `verify update repeat mode`() = runTest {
        // Given
        setupQueue()

        // When
        service.updateRepeatMode(RepeatMode.REPEAT_ALL)

        // Then
        assertEquals(service.repeatMode.value, RepeatMode.REPEAT_ALL)
        verify(exactly = 0) {
            callback.onAppend(any<Song>())
            callback.onAppend(any<List<Song>>())
            callback.onUpdate(any(), any())
            callback.onMove(any(), any())
            callback.onClear()
            callback.onSetQueue(any(), any())
        }
    }
}