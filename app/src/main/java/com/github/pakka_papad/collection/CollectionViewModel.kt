package com.github.pakka_papad.collection

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.data.DataManager
import com.github.pakka_papad.data.music.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val context: Application,
    private val manager: DataManager,
) : ViewModel() {

    val currentSong = manager.currentSong
    val queue = manager.queue

    private val _collectionType = MutableStateFlow<CollectionType?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val collectionUi = _collectionType
        .flatMapLatest { type ->
            when (type?.type) {
                CollectionType.AlbumType -> {
                    manager.getAlbumWithSongsByName(type.id).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.album.name,
                                topBarBackgroundImageUri = it.album.albumArtUri ?: ""
                            )
                        }
                    }
                }
                CollectionType.ArtistType -> {
                    manager.getArtistWithSongsByName(type.id).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.artist.name,
                            )
                        }
                    }
                }
                CollectionType.PlaylistType -> {
                    manager.getPlaylistWithSongsById(type.id.toLong()).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.playlist.playlistName,
                            )
                        }
                    }
                }
                CollectionType.AlbumArtistType -> {
                    manager.getAlbumArtistWithSings(type.id).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.albumArtist.name,
                            )
                        }
                    }
                }
                CollectionType.ComposerType -> {
                    manager.getComposerWithSongs(type.id).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.composer.name,
                            )
                        }
                    }
                }
                CollectionType.LyricistType -> {
                    manager.getLyricistWithSongs(type.id).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.lyricist.name,
                            )
                        }
                    }
                }
                CollectionType.GenreType -> {
                    manager.getGenreWithSongs(type.id).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.genre.genre,
                            )
                        }
                    }
                }
                CollectionType.FavouritesType -> {
                    manager.getFavourites().map {
                        CollectionUi(
                            songs = it,
                            topBarTitle = "Favourites",
                        )
                    }
                }
                else -> flow { }
            }
        }.catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(100),
            initialValue = null
        )

    fun loadCollection(type: CollectionType?) {
        _collectionType.update { type }
    }

    fun shufflePlay(songs: List<Song>?) = setQueue(songs?.shuffled(), 0)

    fun setQueue(songs: List<Song>?, startPlayingFromIndex: Int = 0) {
        if (songs == null) return
        manager.setQueue(songs, startPlayingFromIndex)
        Toast.makeText(context,"Playing",Toast.LENGTH_SHORT).show()
    }

    fun addToQueue(song: Song) {
        if (queue.isEmpty()) {
            manager.setQueue(listOf(song), 0)
        } else {
            val result = manager.addToQueue(song)
            Toast.makeText(
                context,
                if (result) "Added ${song.title} to queue" else "Song already in queue",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun addToQueue(songs: List<Song>) {
        if (queue.isEmpty()) {
            manager.setQueue(songs, 0)
        } else {
            var result = false
            songs.forEach { result = result or manager.addToQueue(it) }
            Toast.makeText(
                context,
                if (result) "Done" else "Song already in queue",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun changeFavouriteValue(song: Song? = currentSong.value) {
        if (song == null) return
        val updatedSong = song.copy(favourite = !song.favourite)
        viewModelScope.launch(Dispatchers.IO) {
            manager.updateSong(updatedSong)
        }
    }

}