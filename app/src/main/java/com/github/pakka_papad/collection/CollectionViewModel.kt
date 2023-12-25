package com.github.pakka_papad.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.Constants
import com.github.pakka_papad.R
import com.github.pakka_papad.components.SortOptions
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.data.services.PlayerService
import com.github.pakka_papad.data.services.PlaylistService
import com.github.pakka_papad.data.services.QueueService
import com.github.pakka_papad.data.services.SongService
import com.github.pakka_papad.util.MessageStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val messageStore: MessageStore,
    private val playlistService: PlaylistService,
    private val songService: SongService,
    private val playerService: PlayerService,
    private val queueService: QueueService,
) : ViewModel() {

    val currentSong = queueService.currentSong
    private val queue = queueService.queue

    private val _collectionType = MutableStateFlow<CollectionType?>(null)

    private val _chosenSortOrder = MutableStateFlow(SortOptions.Default.ordinal)
    val chosenSortOrder = _chosenSortOrder.asStateFlow()

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val collectionUi = _collectionType
        .flatMapLatest { type ->
            when (type?.type) {
                CollectionType.AlbumType -> {
                    songService.getAlbumWithSongsByName(type.id).map {
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
                    songService.getArtistWithSongsByName(type.id).map {
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
                    playlistService.getPlaylistWithSongsById(type.id.toLong()).map {
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
                    songService.getAlbumArtistWithSongsByName(type.id).map {
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
                    songService.getComposerWithSongsByName(type.id).map {
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
                    songService.getLyricistWithSongsByName(type.id).map {
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
                    songService.getGenreWithSongsByName(type.id).map {
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
                    songService.getFavouriteSongs().map {
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
        queueService.setQueue(songs, startPlayingFromIndex)
        playerService.startServiceIfNotRunning(songs, startPlayingFromIndex)
        showMessage(messageStore.getString(R.string.playing))
    }

    fun addToQueue(song: Song) {
        if (queue.isEmpty()) {
            queueService.setQueue(listOf(song), 0)
            playerService.startServiceIfNotRunning(listOf(song), 0)
        } else {
            val result = queueService.append(song)
            showMessage(
                if (result) messageStore.getString(R.string.added_to_queue, song.title)
                else messageStore.getString(R.string.song_already_in_queue)
            )
        }
    }

    fun addToQueue(songs: List<Song>) {
        if (queue.isEmpty()) {
            queueService.setQueue(songs, 0)
            playerService.startServiceIfNotRunning(songs, 0)
        } else {
            val result = queueService.append(songs)
            showMessage(messageStore.getString(if (result) R.string.done else R.string.song_already_in_queue))
        }
    }

    fun changeFavouriteValue(song: Song? = currentSong.value) {
        if (song == null) return
        val updatedSong = song.copy(favourite = !song.favourite)
        viewModelScope.launch(Dispatchers.IO) {
            queueService.update(updatedSong)
            songService.updateSong(updatedSong)
        }
    }

    fun removeFromPlaylist(song: Song){
        viewModelScope.launch {
            try {
                val playlistId = _collectionType.value?.id?.toLong() ?: throw IllegalArgumentException()
                playlistService.removeSongsFromPlaylist(listOf(song.location), playlistId)
                showMessage(messageStore.getString(R.string.done))
            } catch (e: Exception){
                Timber.e(e)
                showMessage(messageStore.getString(R.string.some_error_occurred))
            }
        }
    }

    fun updateSortOrder(order: Int){
        _chosenSortOrder.update { order }
    }

    private fun showMessage(message: String){
        viewModelScope.launch {
            _message.update { message }
            delay(Constants.MESSAGE_DURATION)
            _message.update { "" }
        }
    }
}