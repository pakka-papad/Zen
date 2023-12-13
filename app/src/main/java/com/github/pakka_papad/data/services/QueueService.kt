package com.github.pakka_papad.data.services

import com.github.pakka_papad.data.music.Song
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.TreeSet

interface QueueService {
    val queue: Flow<List<Song>>

    val currentSong: Flow<Song?>

    fun append(song: Song): Boolean
    fun append(songs: List<Song>): Boolean
    fun update(song: Song): Boolean
    fun moveSong(initialPosition: Int, finalPosition: Int): Boolean

    fun clearQueue()
    fun setQueue(songs: List<Song>, startPlayingFromPosition: Int)

    interface Listener {
        fun onAppend(song: Song)
        fun onAppend(songs: List<Song>)
        fun onMove(from: Int, to: Int)
        fun onClear()
        fun onSetQueue(songs: List<Song>, startPlayingFromPosition: Int)
    }

    val callbacks: List<Listener>

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)
}

class QueueServiceImpl() : QueueService {

    private val queueChannel = Channel<List<Song>>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val currentSongChannel = Channel<Song?>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val _queue = ArrayList<Song>()
    private val locations = TreeSet<String>()

    override val queue: Flow<List<Song>> = queueChannel.receiveAsFlow()

    override val currentSong: Flow<Song?> = currentSongChannel.receiveAsFlow()

    override fun append(song: Song): Boolean {
        if (locations.contains(song.location)) return false
        locations.add(song.location)
        _queue.add(song)
        queueChannel.trySend(_queue.toList())
        callbacks.forEach { it.onAppend(song) }
        return true
    }

    override fun append(songs: List<Song>): Boolean {
        if (songs.any { locations.contains(it.location) }) return false
        songs.forEach {
            _queue.add(it)
            locations.add(it.location)
        }
        queueChannel.trySend(_queue.toList())
        callbacks.forEach { it.onAppend(songs) }
        return true
    }

    override fun update(song: Song): Boolean {
        if (!locations.contains(song.location)) return false
        val idx = _queue.indexOfFirst { it.location == song.location }
        if (idx == -1) return false
        _queue[idx] = song
        queueChannel.trySend(_queue.toList())
        return true
    }

    override fun moveSong(initialPosition: Int, finalPosition: Int): Boolean {
        if (initialPosition < 0 || initialPosition >= _queue.size) return false
        if (finalPosition < 0 || finalPosition >= _queue.size) return false
        if (initialPosition == finalPosition) return false
        _queue.apply {
            add(finalPosition, removeAt(initialPosition))
        }
        queueChannel.trySend(_queue.toList())
        callbacks.forEach { it.onMove(initialPosition, finalPosition) }
        return true
    }

    override fun clearQueue() {
        _queue.clear()
        queueChannel.trySend(_queue.toList())
        currentSongChannel.trySend(null)
        callbacks.forEach { it.onClear() }
    }

    override fun setQueue(songs: List<Song>, startPlayingFromPosition: Int) {
        require(songs.isNotEmpty())
        _queue.clear()
        _queue.addAll(songs)
        queueChannel.trySend(_queue.toList())
        val idx = if (startPlayingFromPosition >= songs.size || startPlayingFromPosition < 0) 0
            else startPlayingFromPosition
        callbacks.forEach { it.onSetQueue(songs, idx) }
    }

    override val callbacks = ArrayList<QueueService.Listener>()

    override fun addListener(listener: QueueService.Listener) {
        callbacks.add(listener)
    }

    override fun removeListener(listener: QueueService.Listener) {
        callbacks.remove(listener)
    }
}