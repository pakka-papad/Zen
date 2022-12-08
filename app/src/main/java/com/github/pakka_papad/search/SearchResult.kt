package com.github.pakka_papad.search

import com.github.pakka_papad.data.music.*

data class SearchResult(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val albumArtists: List<AlbumArtist> = emptyList(),
    val composers: List<Composer> = emptyList(),
    val lyricists: List<Lyricist> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val errorMsg: String? = null
)