package com.github.pakka_papad.collection

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.components.SortOptions
import com.github.pakka_papad.data.DataManager
import com.github.pakka_papad.data.music.PlaylistSongCrossRef
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

    private val _chosenSortOrder = MutableStateFlow(SortOptions.Default.ordinal)
    val chosenSortOrder = _chosenSortOrder.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val collectionUi = _collectionType
        .flatMapLatest { type ->
            when (type?.type) {
                CollectionType.AlbumType -> {
                    manager.findCollection.getAlbumWithSongsByName(type.id).map {
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
                    manager.findCollection.getArtistWithSongsByName(type.id).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.artist.name,
                                topBarBackgroundImageUri = it.songs.randomOrNull()?.artUri ?: ""
                            )
                        }
                    }
                }
                CollectionType.PlaylistType -> {
                    manager.findCollection.getPlaylistWithSongsById(type.id.toLong()).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.playlist.playlistName,
                                topBarBackgroundImageUri = it.songs.randomOrNull()?.artUri ?: ""
                            )
                        }
                    }
                }
                CollectionType.AlbumArtistType -> {
                    manager.findCollection.getAlbumArtistWithSings(type.id).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.albumArtist.name,
                                topBarBackgroundImageUri = it.songs.randomOrNull()?.artUri ?: ""
                            )
                        }
                    }
                }
                CollectionType.ComposerType -> {
                    manager.findCollection.getComposerWithSongs(type.id).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.composer.name,
                                topBarBackgroundImageUri = it.songs.randomOrNull()?.artUri ?: ""
                            )
                        }
                    }
                }
                CollectionType.LyricistType -> {
                    manager.findCollection.getLyricistWithSongs(type.id).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.lyricist.name,
                                topBarBackgroundImageUri = it.songs.randomOrNull()?.artUri ?: ""
                            )
                        }
                    }
                }
                CollectionType.GenreType -> {
                    manager.findCollection.getGenreWithSongs(type.id).map {
                        if (it == null) CollectionUi()
                        else {
                            CollectionUi(
                                songs = it.songs,
                                topBarTitle = it.genre.genre,
                                topBarBackgroundImageUri = it.songs.randomOrNull()?.artUri ?: ""
                            )
                        }
                    }
                }
                CollectionType.FavouritesType -> {
                    manager.findCollection.getFavourites().map {
                        CollectionUi(
                            songs = it,
                            topBarTitle = "Favourites",
                            topBarBackgroundImageUri = it.randomOrNull()?.artUri ?: ""
                        )
                    }
                }
                else -> flow { }
            }
        }.combine(_chosenSortOrder) { ui, sortOrder ->
            when(sortOrder){
                SortOptions.TitleASC.ordinal -> {
                    ui.copy(songs = ui.songs.sortedBy { it.title })
                }
                SortOptions.TitleDSC.ordinal -> {
                    ui.copy(songs = ui.songs.sortedByDescending { it.title })
                }
                SortOptions.YearASC.ordinal -> {
                    ui.copy(songs = ui.songs.sortedBy { it.year })
                }
                SortOptions.YearDSC.ordinal -> {
                    ui.copy(songs = ui.songs.sortedByDescending { it.year })
                }
                SortOptions.DurationASC.ordinal -> {
                    ui.copy(songs = ui.songs.sortedBy { it.durationMillis })
                }
                SortOptions.DurationDSC.ordinal -> {
                    ui.copy(songs = ui.songs.sortedByDescending { it.durationMillis })
                }
                else -> ui
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

    fun removeFromPlaylist(song: Song){
        viewModelScope.launch {
            try {
                val playlistId = _collectionType.value?.id?.toLong() ?: throw IllegalArgumentException()
                val playlistSongCrossRef = PlaylistSongCrossRef(playlistId,song.location)
                manager.deletePlaylistSongCrossRef(playlistSongCrossRef)
                Toast.makeText(context,"Removed",Toast.LENGTH_SHORT).show()
            } catch (e: Exception){
                Timber.e(e)
                Toast.makeText(context,"Some error occurred",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateSortOrder(order: Int){
        _chosenSortOrder.update { order }
    }

}