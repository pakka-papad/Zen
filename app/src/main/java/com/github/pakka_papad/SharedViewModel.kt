package com.github.pakka_papad

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.github.pakka_papad.collection.CollectionType
import com.github.pakka_papad.collection.CollectionUi
import com.github.pakka_papad.data.DataManager
import com.github.pakka_papad.data.ZenPreferencesDatastore
import com.github.pakka_papad.data.music.*
import com.github.pakka_papad.ui.theme.ThemePreference
import com.github.pakka_papad.search.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val context: Application,
    private val manager: DataManager,
    private val exoPlayer: ExoPlayer,
    private val datastore: ZenPreferencesDatastore
) : ViewModel() {

    val songs = manager.allSongs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val albumsWithSongs = manager.allAlbumsWithSongs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val artistsWithSongs = manager.allArtistsWithSongs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val playlists = manager.allPlaylists.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val currentSong = manager.currentSong

    val queue = manager.queue

    val scanStatus = manager.scanStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 300,
                replayExpirationMillis = 0
            ),
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
    }

    /**
     * Shuffle the queue and start playing from first song
     */
    fun shufflePlay(songs: List<Song>?) = setQueue(songs?.shuffled(), 0)

    /**
     * The collection to display in collection fragment
     */
    private val _collectionUi = MutableStateFlow<CollectionUi?>(null)
    val collectionUi = _collectionUi.asStateFlow()

    fun loadCollection(type: CollectionType?) =
        viewModelScope.launch {
            _collectionUi.update { null }
            try {
                when (type) {
                    is CollectionType.AlbumType -> {
                        manager.getAlbumWithSongsByName(type.albumName).collect {
                            val result = it
                            if (result == null) {
                                _collectionUi.update { CollectionUi(error = "Could not find the album") }
                            } else {
                                _collectionUi.update {
                                    CollectionUi(
                                        songs = result.songs,
                                        topBarTitle = result.album.name,
                                        topBarBackgroundImageUri = result.album.albumArtUri ?: ""
                                    )
                                }
                            }
                        }
                    }
                    is CollectionType.ArtistType -> {
                        manager.getArtistWithSongsByName(type.artistName).collect {
                            val result = it
                            if (result == null) {
                                _collectionUi.update { CollectionUi(error = "Could not find the artist") }
                            } else {
                                _collectionUi.update {
                                    CollectionUi(
                                        songs = result.songs,
                                        topBarTitle = result.artist.name,
                                    )
                                }
                            }
                        }
                    }
                    is CollectionType.PlaylistType -> {
                        manager.getPlaylistWithSongsById(type.id).collect {
                            val result = it
                            if (result == null) {
                                _collectionUi.update { CollectionUi(error = "Could not find playlist") }
                            } else {
                                _collectionUi.update {
                                    CollectionUi(
                                        songs = result.songs,
                                        topBarTitle = result.playlist.playlistName
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        _collectionUi.update { CollectionUi(error = "Don't know what just happened") }
                    }
                }
            } catch (e: Exception) {
                _collectionUi.update { CollectionUi(error = "Just ran into some errors") }
                Timber.d(e.message)
            }
        }

    fun onPlaylistCreate(playlistName: String) {
        viewModelScope.launch {
            manager.createPlaylist(playlistName)
        }
    }

    private val _selectList = mutableStateListOf<Boolean>()
    val selectList: List<Boolean> = _selectList

    fun updateSelectListSize(size: Int) {
        if (size == _selectList.size) return
        while (size > _selectList.size) {
            _selectList.add(false)
        }
        while (size < _selectList.size) {
            _selectList.removeLast()
        }
    }

    fun toggleSelectAtIndex(index: Int) {
        if (index >= _selectList.size) return
        _selectList[index] = !_selectList[index]
    }

    fun resetSelectList() {
        _selectList.indices.forEach {
            _selectList[it] = false
        }
    }

    fun addSongToPlaylists(songLocation: String) {
        viewModelScope.launch {
            val playlists = playlists.value
            val playlistSongCrossRefs = _selectList.indices
                .filter { _selectList[it] }
                .map {
                    PlaylistSongCrossRef(playlists[it].playlistId, songLocation)
                }
            manager.insertPlaylistSongCrossRefs(playlistSongCrossRefs)
            resetSelectList()
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
    }

    /**
     * Toggle the favourite value of a song
     */
    fun changeFavouriteValue(song: Song? = currentSong.value) {
        if (song == null) return
        val updatedSong = song.copy(favourite = !song.favourite)
//        if (_collectionUi.value?.songs?.any { it.location == song.location } == true) {
//            _collectionUi.update {
//                _collectionUi.value!!.copy(
//                    songs = _collectionUi.value!!.songs.map {
//                        if (it.location == song.location) updatedSong else it
//                    }
//                )
//            }
//        }
        viewModelScope.launch(Dispatchers.IO) {
            manager.updateSong(updatedSong)
        }
    }

    private val _searchResult = MutableStateFlow(SearchResult())
    val searchResult = _searchResult.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private var lastJob: Job? = null
    fun search(query: String){
        lastJob?.cancel()
        lastJob = viewModelScope.launch {
            _query.update { query }
            val trimmedQuery = query.trim()
            if (trimmedQuery.isEmpty()) {
                _searchResult.update { SearchResult() }
                return@launch
            }
            try {
                lateinit var l1: List<Song>
                lateinit var l2: List<Album>
                lateinit var l3: List<Artist>
                lateinit var l4: List<AlbumArtist>
                lateinit var l5: List<Composer>
                lateinit var l6: List<Lyricist>
                lateinit var l7: List<Genre>
                lateinit var l8: List<Playlist>

                val searchJobs = listOf(
                    launch { l1 = manager.searchSongs(trimmedQuery) },
                    launch { l2 = manager.searchAlbums(trimmedQuery) },
                    launch { l3 = manager.searchArtists(trimmedQuery) },
                    launch { l4 = manager.searchAlbumArtists(trimmedQuery) },
                    launch { l5 = manager.searchComposers(trimmedQuery) },
                    launch { l6 = manager.searchLyricists(trimmedQuery) },
                    launch { l7 = manager.searchGenres(trimmedQuery) },
                    launch { l8 = manager.searchPlaylists(trimmedQuery) },
                )
                searchJobs.joinAll()
                _searchResult.update {
                    SearchResult(l1,l2,l3,l4,l5,l6,l7,l8)
                }
            } catch (e: Exception){
                Timber.d(e.message)
            }
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
            datastore.setTheme(themePreference.useMaterialYou, themePreference.theme)
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