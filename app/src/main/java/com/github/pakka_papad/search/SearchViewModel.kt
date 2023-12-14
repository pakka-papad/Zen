package com.github.pakka_papad.search

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.data.services.PlayerService
import com.github.pakka_papad.data.services.QueueService
import com.github.pakka_papad.data.services.SearchService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val context: Application,
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
//        manager.setQueue(songs, startPlayingFromIndex)
        queueService.setQueue(songs, startPlayingFromIndex)
        playerService.startServiceIfNotRunning(songs, startPlayingFromIndex)
        Toast.makeText(context, "Playing", Toast.LENGTH_SHORT).show()
    }

}