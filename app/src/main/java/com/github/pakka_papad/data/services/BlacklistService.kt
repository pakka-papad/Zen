package com.github.pakka_papad.data.services

import com.github.pakka_papad.data.daos.AlbumArtistDao
import com.github.pakka_papad.data.daos.AlbumDao
import com.github.pakka_papad.data.daos.ArtistDao
import com.github.pakka_papad.data.daos.BlacklistDao
import com.github.pakka_papad.data.daos.BlacklistedFolderDao
import com.github.pakka_papad.data.daos.ComposerDao
import com.github.pakka_papad.data.daos.GenreDao
import com.github.pakka_papad.data.daos.LyricistDao
import com.github.pakka_papad.data.daos.SongDao
import com.github.pakka_papad.data.music.BlacklistedFolder
import com.github.pakka_papad.data.music.BlacklistedSong
import com.github.pakka_papad.data.music.Song
import kotlinx.coroutines.flow.Flow

interface BlacklistService {
    val blacklistedSongs: Flow<List<BlacklistedSong>>
    val blacklistedFolders: Flow<List<BlacklistedFolder>>

    suspend fun blacklistSong(vararg songs: Song)
    suspend fun whitelistSong(vararg blacklistedSongs: BlacklistedSong)

    suspend fun blacklistFolder(vararg folderPaths: String)
    suspend fun whitelistFolder(vararg folders: BlacklistedFolder)
}

class BlacklistServiceImpl(
    private val blacklistDao: BlacklistDao,
    private val blacklistedFolderDao: BlacklistedFolderDao,
    private val songDao: SongDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val albumArtistDao: AlbumArtistDao,
    private val composerDao: ComposerDao,
    private val lyricistDao: LyricistDao,
    private val genreDao: GenreDao,
): BlacklistService {

    override val blacklistedSongs: Flow<List<BlacklistedSong>>
         = blacklistDao.getBlacklistedSongsFlow()

    override val blacklistedFolders: Flow<List<BlacklistedFolder>>
         = blacklistedFolderDao.getAllFolders()

    override suspend fun blacklistSong(vararg songs: Song) {
        songs.forEach { song ->
            songDao.deleteSong(song)
            blacklistDao.addSong(
                BlacklistedSong(
                    location = song.location,
                    title = song.title,
                    artist = song.artist,
                )
            )
        }
    }

    override suspend fun whitelistSong(vararg blacklistedSongs: BlacklistedSong) {
        blacklistedSongs.forEach { blacklistedSong ->
            blacklistDao.deleteBlacklistedSong(blacklistedSong)
        }
    }

    override suspend fun blacklistFolder(vararg folderPaths: String) {
        folderPaths.forEach { folderPath ->
            songDao.deleteSongsWithPathPrefix(folderPath)
            blacklistedFolderDao.insertFolder(BlacklistedFolder(folderPath))
        }
        cleanData()
    }

    private suspend fun cleanData(){
        albumDao.cleanAlbumTable()
        artistDao.cleanArtistTable()
        albumArtistDao.cleanAlbumArtistTable()
        composerDao.cleanComposerTable()
        lyricistDao.cleanLyricistTable()
        genreDao.cleanGenreTable()
    }

    override suspend fun whitelistFolder(vararg folders: BlacklistedFolder) {
        folders.forEach { folder ->
            blacklistedFolderDao.deleteFolder(folder)
        }
    }
}