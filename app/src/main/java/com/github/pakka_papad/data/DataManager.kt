package com.github.pakka_papad.data

import com.github.pakka_papad.data.daos.AlbumArtistDao
import com.github.pakka_papad.data.daos.AlbumDao
import com.github.pakka_papad.data.daos.ArtistDao
import com.github.pakka_papad.data.daos.BlacklistDao
import com.github.pakka_papad.data.daos.BlacklistedFolderDao
import com.github.pakka_papad.data.daos.ComposerDao
import com.github.pakka_papad.data.daos.GenreDao
import com.github.pakka_papad.data.daos.LyricistDao
import com.github.pakka_papad.data.daos.SongDao
import com.github.pakka_papad.data.music.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File

class DataManager(
    private val songDao: SongDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val albumArtistDao: AlbumArtistDao,
    private val composerDao: ComposerDao,
    private val lyricistDao: LyricistDao,
    private val genreDao: GenreDao,
    private val blacklistDao: BlacklistDao,
    private val blacklistedFolderDao: BlacklistedFolderDao,
    private val scope: CoroutineScope,
    private val songExtractor: SongExtractor,
) {
    init {
        cleanData()
    }

    fun cleanData() {
        scope.launch {
            val jobs = mutableListOf<Job>()
            songDao.getSongs().forEach {
                try {
                    if(!File(it.location).exists()){
                        jobs += launch { songDao.deleteSong(it) }
                    }
                } catch (_: Exception){

                }
            }
            jobs.joinAll()
            jobs.clear()
            albumDao.cleanAlbumTable()
            artistDao.cleanArtistTable()
            albumArtistDao.cleanAlbumArtistTable()
            composerDao.cleanComposerTable()
            lyricistDao.cleanLyricistTable()
            genreDao.cleanGenreTable()
        }
    }

    private val _scanStatus = Channel<ScanStatus>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val scanStatus = _scanStatus.receiveAsFlow()

    fun scanForMusic() = scope.launch {
        _scanStatus.send(ScanStatus.ScanStarted)
        val blacklistedSongLocations = blacklistDao
            .getBlacklistedSongs()
            .map { it.location }
            .toHashSet()
        val blacklistedFolderPaths = blacklistedFolderDao
            .getAllFolders()
            .first()
            .map { it.path }
            .toHashSet()
        val (songs, albums) = songExtractor.extract(
            blacklistedSongLocations,
            blacklistedFolderPaths,
            statusListener = { parsed, total ->
                _scanStatus.trySend(ScanStatus.ScanProgress(parsed, total))
            }
        )
        val artists = songs.map { it.artist }.toSet().map { Artist(it) }
        val albumArtists = songs.map { it.albumArtist }.toSet().map { AlbumArtist(it) }
        val lyricists = songs.map { it.lyricist }.toSet().map { Lyricist(it) }
        val composers = songs.map { it.composer }.toSet().map { Composer(it) }
        val genres = songs.map { it.genre }.toSet().map { Genre(it) }
        albumDao.insertAllAlbums(albums)
        artistDao.insertAllArtists(artists)
        albumArtistDao.insertAllAlbumArtists(albumArtists)
        lyricistDao.insertAllLyricists(lyricists)
        composerDao.insertAllComposers(composers)
        genreDao.insertAllGenres(genres)
        songDao.insertAllSongs(songs)
        _scanStatus.send(ScanStatus.ScanComplete)
    }
}