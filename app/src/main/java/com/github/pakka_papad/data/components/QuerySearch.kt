package com.github.pakka_papad.data.components

class QuerySearch(
    private val daoCollection: DaoCollection,
) {
    suspend fun searchSongs(query: String) = daoCollection.songDao.searchSongs(query)

    suspend fun searchAlbums(query: String) = daoCollection.albumDao.searchAlbums(query)

    suspend fun searchArtists(query: String) = daoCollection.artistDao.searchArtists(query)

    suspend fun searchAlbumArtists(query: String) = daoCollection.albumArtistDao.searchAlbumArtists(query)

    suspend fun searchComposers(query: String) = daoCollection.composerDao.searchComposers(query)

    suspend fun searchLyricists(query: String) = daoCollection.lyricistDao.searchLyricists(query)

    suspend fun searchPlaylists(query: String) = daoCollection.playlistDao.searchPlaylists(query)

    suspend fun searchGenres(query: String) = daoCollection.genreDao.searchGenres(query)
}