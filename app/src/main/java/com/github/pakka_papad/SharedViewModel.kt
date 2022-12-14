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
import com.github.pakka_papad.home.Person
import com.github.pakka_papad.search.SearchResult
import com.github.pakka_papad.search.SearchType
import com.github.pakka_papad.ui.theme.ThemePreference
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

    val albums = manager.allAlbums.stateIn(
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
    val personsWithSongCount = _selectedPerson.flatMapLatest {
        when (it) {
            Person.Artist -> manager.allArtistWithSongCount
            Person.AlbumArtist -> manager.allAlbumArtistWithSongCount
            Person.Composer -> manager.allComposerWithSongCount
            Person.Lyricist -> manager.allLyricistWithSongCount
        }
    }.catch { exception ->
        Timber.e(exception)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val playlistsWithSongCount = manager.allPlaylistsWithSongCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val genresWithSongCount = manager.allGenresWithSongCount.stateIn(
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
    private val _type = MutableStateFlow<CollectionType?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val collectionUi = _type.flatMapLatest { type ->
        when (type) {
            is CollectionType.AlbumType -> {
                manager.getAlbumWithSongsByName(type.albumName).map {
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
            is CollectionType.ArtistType -> {
                manager.getArtistWithSongsByName(type.artistName).map {
                    if (it == null) CollectionUi()
                    else {
                        CollectionUi(
                            songs = it.songs,
                            topBarTitle = it.artist.name,
                        )
                    }
                }
            }
            is CollectionType.PlaylistType -> {
                manager.getPlaylistWithSongsById(type.id).map {
                    if (it == null) CollectionUi()
                    else {
                        CollectionUi(
                            songs = it.songs,
                            topBarTitle = it.playlist.playlistName,
                        )
                    }
                }
            }
            is CollectionType.AlbumArtistType -> {
                manager.getAlbumArtistWithSings(type.name).map {
                    if (it == null) CollectionUi()
                    else {
                        CollectionUi(
                            songs = it.songs,
                            topBarTitle = it.albumArtist.name,
                        )
                    }
                }
            }
            is CollectionType.ComposerType -> {
                manager.getComposerWithSongs(type.name).map {
                    if (it == null) CollectionUi()
                    else {
                        CollectionUi(
                            songs = it.songs,
                            topBarTitle = it.composer.name,
                        )
                    }
                }
            }
            is CollectionType.LyricistType -> {
                manager.getLyricistWithSongs(type.name).map {
                    if (it == null) CollectionUi()
                    else {
                        CollectionUi(
                            songs = it.songs,
                            topBarTitle = it.lyricist.name,
                        )
                    }
                }
            }
            is CollectionType.GenreType -> {
                manager.getGenreWithSongs(type.genre).map {
                    if (it == null) CollectionUi()
                    else {
                        CollectionUi(
                            songs = it.songs,
                            topBarTitle = it.genre.genre,
                        )
                    }
                }
            }
            is CollectionType.Favourites -> {
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
        _type.update { type }
    }

    fun onSongDrag(fromIndex: Int, toIndex: Int) = manager.moveItem(fromIndex,toIndex)

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

    fun addSongsToPlaylists(songLocations: Array<String>) {
        viewModelScope.launch {
            val playlists = playlistsWithSongCount.value
            val playlistSongCrossRefs = _selectList.indices
                .filter { _selectList[it] }
                .map {
                    val list = ArrayList<PlaylistSongCrossRef>()
                    for (songLocation in songLocations) {
                        list += PlaylistSongCrossRef(playlists[it].playlistId, songLocation)
                    }
                    list.toList()
                }
            manager.insertPlaylistSongCrossRefs(playlistSongCrossRefs.flatten())
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

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _searchType = MutableStateFlow(SearchType.Songs)
    val searchType = _searchType.asStateFlow()

    val searchResult = _query.combine(searchType) { query, type ->
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) {
            SearchResult()
        } else {
            when (type) {
                SearchType.Songs -> SearchResult(songs = manager.searchSongs(trimmedQuery))
                SearchType.Albums -> SearchResult(albums = manager.searchAlbums(trimmedQuery))
                SearchType.Artists -> SearchResult(artists = manager.searchArtists(trimmedQuery))
                SearchType.AlbumArtists -> SearchResult(albumArtists = manager.searchAlbumArtists(trimmedQuery))
                SearchType.Composers -> SearchResult(composers = manager.searchComposers(trimmedQuery))
                SearchType.Lyricists -> SearchResult(lyricists = manager.searchLyricists(trimmedQuery))
                SearchType.Genres -> SearchResult(genres = manager.searchGenres(trimmedQuery))
                SearchType.Playlists -> SearchResult(playlists = manager.searchPlaylists(trimmedQuery))
            }
        }
    }.catch { exception ->
        Timber.e(exception)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SearchResult()
    )

    fun clearQueryText() {
        _query.update { "" }
    }

    fun updateQuery(query: String) {
        _query.update { query }
    }

    fun updateType(type: SearchType) {
        _searchType.update { type }
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