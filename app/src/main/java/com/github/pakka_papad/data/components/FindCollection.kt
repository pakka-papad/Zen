package com.github.pakka_papad.data.components

class FindCollection(
    private val daoCollection: DaoCollection,
) {
    fun getPlaylistWithSongsById(id: Long) = daoCollection.playlistDao.getPlaylistWithSongs(id)

    fun getAlbumWithSongsByName(albumName: String) = daoCollection.albumDao.getAlbumWithSongsByName(albumName)

    fun getArtistWithSongsByName(artistName: String) = daoCollection.artistDao.getArtistWithSongsByName(artistName)

    fun getAlbumArtistWithSings(albumArtistName: String) = daoCollection.albumArtistDao.getAlbumArtistWithSongs(albumArtistName)

    fun getComposerWithSongs(composerName: String) = daoCollection.composerDao.getComposerWithSongs(composerName)

    fun getLyricistWithSongs(lyricistName: String) = daoCollection.lyricistDao.getLyricistWithSongs(lyricistName)

    fun getGenreWithSongs(genreName: String) = daoCollection.genreDao.getGenreWithSongs(genreName)

    fun getFavourites() = daoCollection.songDao.getAllFavourites()
}