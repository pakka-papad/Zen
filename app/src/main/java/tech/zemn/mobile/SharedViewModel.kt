package tech.zemn.mobile

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
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
import tech.zemn.mobile.data.music.Song
import tech.zemn.mobile.data.music.AlbumWithSongs
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

    private val _currentSong = MutableStateFlow<Song?>(null)
    private val _currentSongBitmap = MutableStateFlow<Bitmap?>(null)
    val currentSong = _currentSong.asStateFlow()
    val currentSongBitmap = _currentSongBitmap.asStateFlow()

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
        exoPlayer.addListener(exoPlayerListener)
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.removeListener(exoPlayerListener)
    }

    fun onSongClicked(song: Song) {
        _currentSong.value = song
        val extractor = MediaMetadataRetriever()
        extractor.setDataSource(song.location)
        if (extractor.embeddedPicture != null) {
            _currentSongBitmap.value =
                BitmapFactory.decodeByteArray(
                    extractor.embeddedPicture,
                    0,
                    extractor.embeddedPicture!!.size
                )
        }
        manager.updateQueue(listOf(song))
    }

    fun onAlbumClicked(album: AlbumWithSongs){

    }
}