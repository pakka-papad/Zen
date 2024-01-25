package com.github.pakka_papad.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.Constants
import com.github.pakka_papad.R
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.data.services.PlayerService
import com.github.pakka_papad.data.services.QueueService
import com.github.pakka_papad.data.services.SearchService
import com.github.pakka_papad.util.MessageStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val messageStore: MessageStore,
    private val playerService: PlayerService,
    private val queueService: QueueService,
    private val searchService: SearchService,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _searchType = MutableStateFlow(SearchType.Songs)
    val searchType = _searchType.asStateFlow()

    val searchResult = _query
        .combine(searchType) { query, type ->
            val trimmedQuery = query.trim()
            if (trimmedQuery.isEmpty()) {
                SearchResult()
            } else {
                when (type) {
                    SearchType.Songs -> SearchResult(songs = searchService.searchSongs(trimmedQuery))
                    SearchType.Albums -> SearchResult(albums = searchService.searchAlbums(trimmedQuery))
                    SearchType.Artists -> SearchResult(artists = searchService.searchArtists(trimmedQuery))
                    SearchType.AlbumArtists -> SearchResult(albumArtists = searchService.searchAlbumArtists(trimmedQuery))
                    SearchType.Composers -> SearchResult(composers = searchService.searchComposers(trimmedQuery))
                    SearchType.Lyricists -> SearchResult(lyricists = searchService.searchLyricists(trimmedQuery))
                    SearchType.Genres -> SearchResult(genres = searchService.searchGenres(trimmedQuery))
                    SearchType.Playlists -> SearchResult(playlists = searchService.searchPlaylists(trimmedQuery))
                }
            }
        }.catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchResult()
        )

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    fun clearQueryText() {
        _query.update { "" }
    }

    fun updateQuery(query: String) {
        _query.update { query }
    }

    fun updateType(type: SearchType) {
        _searchType.update { type }
    }

    fun setQueue(songs: List<Song>?, startPlayingFromIndex: Int = 0) {
        if (songs == null) return
//        queueService.setQueue(songs, startPlayingFromIndex)
        viewModelScope.launch {
            playerService.startServiceIfNotRunning(songs, startPlayingFromIndex)
        }
        showMessage(messageStore.getString(R.string.playing))
    }

    private fun showMessage(message: String){
        viewModelScope.launch {
            _message.update { message }
            delay(Constants.MESSAGE_DURATION)
            _message.update { "" }
        }
    }
}