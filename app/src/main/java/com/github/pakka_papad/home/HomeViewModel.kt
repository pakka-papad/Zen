package com.github.pakka_papad.home

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.github.pakka_papad.data.DataManager
import com.github.pakka_papad.data.music.*
import com.github.pakka_papad.storage_explorer.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val context: Application,
    private val manager: DataManager,
    private val exoPlayer: ExoPlayer,
) : ViewModel() {

    val songs = manager.getAll.songs()
        .catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val albums = manager.getAll.albums()
        .catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _selectedPerson = MutableStateFlow(Person.Artist)
    val selectedPerson = _selectedPerson.asStateFlow()

    fun onPersonSelect(person: Person) {
        _selectedPerson.update { person }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val personsWithSongCount = _selectedPerson
        .flatMapLatest {
            when (it) {
                Person.Artist -> manager.getAll.artists()
                Person.AlbumArtist -> manager.getAll.albumArtists()
                Person.Composer -> manager.getAll.composers()
                Person.Lyricist -> manager.getAll.lyricists()
            }
        }.catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val playlistsWithSongCount = manager.getAll.playlists()
        .catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val genresWithSongCount = manager.getAll.genres()
        .catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val currentSong = manager.currentSong

    val queue = manager.queue

    val repeatMode = manager.repeatMode

    fun toggleRepeatMode(){
        manager.updateRepeatMode(repeatMode.value.next())
    }

    private val _currentSongPlaying = MutableStateFlow<Boolean?>(null)
    val currentSongPlaying = _currentSongPlaying.asStateFlow()

    private val exoPlayerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _currentSongPlaying.update { isPlaying }
        }
    }

    init {
        _currentSongPlaying.update { exoPlayer.isPlaying }
        exoPlayer.addListener(exoPlayerListener)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.removeListener(exoPlayerListener)
        explorer.removeListener(directoryChangeListener)
    }

    /**
     * Shuffle the queue and start playing from first song
     */
    fun shufflePlay(songs: List<Song>?) = setQueue(songs?.shuffled(), 0)

    fun onSongBlacklist(song: Song) {
        viewModelScope.launch {
            try {
                manager.deleteSong(song)
                showToast("Done")
            } catch (e: Exception) {
                Timber.e(e)
                showToast("Some error occurred")
            }
        }
    }

    fun onPlaylistCreate(playlistName: String) {
        viewModelScope.launch {
            manager.createPlaylist(playlistName)
        }
    }

    fun deletePlaylist(playlistWithSongCount: PlaylistWithSongCount) {
        viewModelScope.launch {
            try {
                val playlist = Playlist(
                    playlistId = playlistWithSongCount.playlistId,
                    playlistName = playlistWithSongCount.playlistName,
                    createdAt = playlistWithSongCount.createdAt
                )
                manager.deletePlaylist(playlist)
                showToast("Done")
            } catch (e: Exception) {
                Timber.e(e)
                showToast("Some error occurred")
            }
        }
    }

    /**
     * Adds a song to the end of queue
     */
    fun addToQueue(song: Song) {
        if (queue.isEmpty()) {
            manager.setQueue(listOf(song), 0)
        } else {
            val result = manager.addToQueue(song)
            if (result) {
                showToast("Added ${song.title} to queue")
            } else {
                showToast("Song already in queue")
            }
        }
    }

    /**
     * Adds a list of songs to the end queue
     */
    fun addToQueue(songs: List<Song>) {
        if (queue.isEmpty()) {
            manager.setQueue(songs, 0)
        } else {
            var result = false
            songs.forEach { result = result or manager.addToQueue(it) }
            if (result) {
                showToast("Done")
            } else {
                showToast("Songs already in queue")
            }
        }
    }

    /**
     * Create and set a new queue in exoplayer.
     * Old queue is discarded.
     * Playing starts immediately
     * @param songs queue items
     * @param startPlayingFromIndex index of song from which playing should start
     */
    fun setQueue(songs: List<Song>?, startPlayingFromIndex: Int = 0) {
        if (songs == null) return
        manager.setQueue(songs, startPlayingFromIndex)
        showToast("Playing")
    }

    /**
     * Toggle the favourite value of a song
     */
    fun changeFavouriteValue(song: Song? = currentSong.value) {
        if (song == null) return
        val updatedSong = song.copy(favourite = !song.favourite)
        viewModelScope.launch(Dispatchers.IO) {
            manager.updateSong(updatedSong)
        }
    }

    fun onSongDrag(fromIndex: Int, toIndex: Int) = manager.moveItem(fromIndex,toIndex)


    private val _filesInCurrentDestination = MutableStateFlow(listOf<StorageFile>())
    val filesInCurrentDestination = _filesInCurrentDestination.asStateFlow()

    private val explorer = MusicFileExplorer()

    private val directoryChangeListener = object : MusicFileExplorer.DirectoryChangeListener {
        override fun onDirectoryChanged(path: String, files: Array<File>) {
            val storageFiles = files.map {
                if (it.isDirectory){
                    StorageFile.Folder(it.name,it.absolutePath)
                } else {
                    StorageFile.MusicFile(it.name,it.absolutePath)
                }
            }
            Timber.d(storageFiles.toString())
            _filesInCurrentDestination.update { storageFiles }
        }
    }

    init {
        explorer.addListener(directoryChangeListener)
    }

    fun onFileClicked(file: StorageFile){
        if (!file.isDirectory) return
        explorer.moveInsideDirectory(file.absolutePath)
    }

    fun moveToParent() {
        explorer.moveToParent()
    }
}