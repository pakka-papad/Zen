package com.github.pakka_papad.data.components

import com.github.pakka_papad.data.daos.*

data class DaoCollection(
    val songDao: SongDao,
    val albumDao: AlbumDao,
    val artistDao: ArtistDao,
    val albumArtistDao: AlbumArtistDao,
    val composerDao: ComposerDao,
    val lyricistDao: LyricistDao,
    val genreDao: GenreDao,
    val playlistDao: PlaylistDao,
    val blacklistDao: BlacklistDao,
    val blacklistedFolderDao: BlacklistedFolderDao,
)