package com.github.pakka_papad.data.components

class GetAll(
    private val daoCollection: DaoCollection,
) {
    fun songs() = daoCollection.songDao.getAllSongs()

    fun albums() = daoCollection.albumDao.getAllAlbums()

    fun artists() = daoCollection.songDao.getAllArtistsWithSongCount()

    fun albumArtists() = daoCollection.songDao.getAllAlbumArtistsWithSongCount()

    fun composers() = daoCollection.songDao.getAllComposersWithSongCount()

    fun lyricists() = daoCollection.songDao.getAllLyricistsWithSongCount()

    fun playlists() = daoCollection.playlistDao.getAllPlaylistWithSongCount()

    fun genres() = daoCollection.songDao.getAllGenresWithSongCount()

    fun blacklistedSongs() = daoCollection.blacklistDao.getBlacklistedSongsFlow()
}