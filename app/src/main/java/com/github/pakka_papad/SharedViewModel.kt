package com.github.pakka_papad

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.github.pakka_papad.data.DataManager
import com.github.pakka_papad.data.ZenPreferencesDatastore
import com.github.pakka_papad.data.music.AlbumWithSongs
import com.github.pakka_papad.data.music.ArtistWithSongs
import com.github.pakka_papad.data.music.ScanStatus
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.playlist.PlaylistUi
import com.github.pakka_papad.ui.theme.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val context: Application,
    private val manager: DataManager,
    private val exoPlayer: ExoPlayer,
    private val datastore: ZenPreferencesDatastore
) : ViewModel() {

    private val _songs = MutableStateFlow(listOf<Song>())
    val songs = _songs.asStateFlow()

    private val _albumsWithSongs = MutableStateFlow(listOf<AlbumWithSongs>())
    val albumsWithSongs = _albumsWithSongs.asStateFlow()

    private val _artistsWithSongs = MutableStateFlow(listOf<ArtistWithSongs>())
    val artistsWithSongs = _artistsWithSongs.asStateFlow()

    val currentSong = manager.currentSong

    val queue = manager.queue

    init {
        viewModelScope.launch {
            manager.allSongs.collect {
                _songs.value = it
            }
        }
        viewModelScope.launch {
            manager.allAlbums.collect {
                _albumsWithSongs.value = it
            }
        }
        viewModelScope.launch {
            manager.allArtists.collect {
                _artistsWithSongs.value = it
            }
        }
    }

    val scanStatus = manager.scanStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 1500, replayExpirationMillis = 0),
            initialValue = ScanStatus.ScanNotRunning
        )

    fun scanForMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            manager.scanForMusic()
        }
    }

    private val _currentSongPlaying = MutableStateFlow<Boolean?>(null)
    val currentSongPlaying = _currentSongPlaying.asStateFlow()

    private val exoPlayerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _currentSongPlaying.value = exoPlayer.isPlaying
        }
    }

    init {
        _currentSongPlaying.value = exoPlayer.isPlaying
        exoPlayer.addListener(exoPlayerListener)
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.removeListener(exoPlayerListener)
    }

    /**
     * Shuffle the queue and start playing from first song
     */
    fun shufflePlay(songs: List<Song>) = setQueue(songs.shuffled(),0)

    /**
     * The playlist to display in playlist fragment
     */
    private val _playlist = MutableStateFlow(PlaylistUi())
    val playlist = _playlist.asStateFlow()

    fun onAlbumClicked(albumWithSongs: AlbumWithSongs) {
        _playlist.value = PlaylistUi(
            songs = albumWithSongs.songs,
            topBarTitle = albumWithSongs.album.name,
            topBarBackgroundImageUri = albumWithSongs.album.albumArtUri ?: ""
        )
    }

    fun onArtistClicked(artistWithSongs: ArtistWithSongs) {
        _playlist.value = PlaylistUi(
            songs = artistWithSongs.songs,
            topBarTitle = artistWithSongs.artist.name,
        )
    }

    /**
     * Adds a song to the end of queue
     */
    fun addToQueue(song: Song) {
        if (queue.value.isEmpty()) {
            manager.setQueue(listOf(song),0)
        } else {
            manager.addToQueue(song)
        }
    }

    /**
     * Adds a list of songs to the end queue
     */
    fun addToQueue(songs: List<Song>) {
        if (queue.value.isEmpty()){
            manager.setQueue(songs,0)
        } else {
            songs.forEach { manager.addToQueue(it) }
        }
    }

    /**
     * Create and set a new queue in exoplayer.
     * Old queue is discarded.
     * Playing starts immediately
     * @param songs queue items
     * @param startPlayingFromIndex index of song from which playing should start
     */
    fun setQueue(songs: List<Song>, startPlayingFromIndex: Int = 0) {
        manager.setQueue(songs, startPlayingFromIndex)
    }

    /**
     * Toggle the favourite value of a song
     */
    fun changeFavouriteValue(song: Song? = currentSong.value) {
        if (song == null) return
        val updatedSong = song.copy(favourite = !song.favourite)
        if (_playlist.value.songs.any { it.location == song.location }) {
            _playlist.value = _playlist.value.copy(
                songs = _playlist.value.songs.map {
                    if (it.location == song.location) updatedSong else it
                }
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            manager.updateSong(updatedSong)
        }
    }

    val theme = datastore.preferences.map {
        ThemePreference(
            useMaterialYou = it.useMaterialYouTheme,
            theme = it.chosenTheme
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = ThemePreference(),
        started = SharingStarted.Eagerly
    )

    fun updateTheme(themePreference: ThemePreference) {
        viewModelScope.launch(Dispatchers.IO) {
            datastore.setTheme(themePreference.useMaterialYou,themePreference.theme)
        }
    }

    val isOnBoardingComplete = datastore.preferences.map {
        it.onBoardingComplete
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    fun setOnBoardingComplete() {
        viewModelScope.launch(Dispatchers.IO) {
            datastore.setOnBoardingComplete()
        }
    }
}