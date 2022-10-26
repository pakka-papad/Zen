package tech.zemn.mobile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.zemn.mobile.data.DataManager
import tech.zemn.mobile.data.music.AlbumWithSongs
import tech.zemn.mobile.data.music.ArtistWithSongs
import tech.zemn.mobile.data.music.Song
import tech.zemn.mobile.playlist.PlaylistUi
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val context: Application,
    private val manager: DataManager,
    private val exoPlayer: ExoPlayer,
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
        viewModelScope.launch(Dispatchers.IO) {
//            manager.scanForMusic()
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

    fun onSongClicked(index: Int, song: Song) {
        manager.setQueue(listOf(song),0)
    }

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

    fun addToQueue(song: Song) {
        manager.addToQueue(song)
    }

    fun addToQueue(songs: List<Song>) {
        songs.forEach { manager.addToQueue(it) }
    }

    fun setQueue(songs: List<Song>, startPlayingFromIndex: Int = 0) {
        manager.setQueue(songs, startPlayingFromIndex)
    }

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
}