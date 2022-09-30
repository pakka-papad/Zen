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
import tech.zemn.mobile.data.music.Album
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val context: Application,
    private val manager: DataManager,
    private val exoPlayer: ExoPlayer,
) : ViewModel() {

    private val _songs = MutableStateFlow(listOf<Song>())
    val songs = _songs.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    private val _currentSongBitmap = MutableStateFlow<Bitmap?>(null)
    val currentSong = _currentSong.asStateFlow()
    val currentSongBitmap = _currentSongBitmap.asStateFlow()

    init {
        viewModelScope.launch {
            manager.allSongs.collect {
                _songs.value = it
                launch(Dispatchers.Default) {
                    computeAlbumData(it)
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
//            manager.scanForMusic()
        }
    }

    private val albumMap = HashMap<String, Album>()
    private val _albums = MutableStateFlow<List<Album>>(listOf())
    val albums = _albums.asStateFlow()

    private fun computeAlbumData(songs: List<Song>) {
        val imageMap = HashMap<String,Bitmap?>()
        val songsMap = HashMap<String,ArrayList<Song>>()
        songs.forEach { song ->
            if (!imageMap.containsKey(song.album)){
                imageMap[song.album] = null
            }
            if (imageMap[song.album] == null) {
                val extractor = MediaMetadataRetriever()
                extractor.setDataSource(song.location)
                if (extractor.embeddedPicture != null) {
                    imageMap[song.album] =
                        BitmapFactory.decodeByteArray(
                            extractor.embeddedPicture,
                            0,
                            extractor.embeddedPicture!!.size
                        )
                }
            }
            if (!songsMap.containsKey(song.album)){
                songsMap[song.album] = ArrayList()
            }
            songsMap[song.album]!!.add(song)
        }
        val result = ArrayList<Album>()
        songsMap.forEach { (albumName, songList) ->
            val album = Album(
                name = albumName,
                songs = songList,
                albumArt = imageMap[albumName]
            )
            result.add(album)
            albumMap[album.name] = album
        }
        result.sortBy { it.name }
        _albums.value = result
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

    fun onAlbumClicked(album: String){

    }
}