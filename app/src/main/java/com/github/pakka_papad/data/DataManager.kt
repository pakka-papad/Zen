package com.github.pakka_papad.data

import android.content.Context
import com.github.pakka_papad.data.components.*
import com.github.pakka_papad.data.music.*
import com.github.pakka_papad.data.notification.ZenNotificationManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File

class DataManager(
    private val context: Context,
    private val notificationManager: ZenNotificationManager,
    private val daoCollection: DaoCollection,
    private val scope: CoroutineScope,
    private val songExtractor: SongExtractor,
) {

    val querySearch by lazy { QuerySearch(daoCollection) }

    init {
        cleanData()
    }

    fun cleanData() {
        scope.launch {
            val jobs = mutableListOf<Job>()
            daoCollection.songDao.getSongs().forEach {
                try {
                    if(!File(it.location).exists()){
                        jobs += launch { daoCollection.songDao.deleteSong(it) }
                    }
                } catch (_: Exception){

                }
            }
            jobs.joinAll()
            jobs.clear()
            daoCollection.albumDao.cleanAlbumTable()
            daoCollection.artistDao.cleanArtistTable()
            daoCollection.albumArtistDao.cleanAlbumArtistTable()
            daoCollection.composerDao.cleanComposerTable()
            daoCollection.lyricistDao.cleanLyricistTable()
            daoCollection.genreDao.cleanGenreTable()
        }
    }

    private val _scanStatus = Channel<ScanStatus>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val scanStatus = _scanStatus.receiveAsFlow()

    fun scanForMusic() = scope.launch {
        _scanStatus.send(ScanStatus.ScanStarted)
        val blacklistedSongLocations = daoCollection.blacklistDao
            .getBlacklistedSongs()
            .map { it.location }
            .toHashSet()
        val blacklistedFolderPaths = daoCollection.blacklistedFolderDao
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
        daoCollection.albumDao.insertAllAlbums(albums)
        daoCollection.artistDao.insertAllArtists(artists)
        daoCollection.albumArtistDao.insertAllAlbumArtists(albumArtists)
        daoCollection.lyricistDao.insertAllLyricists(lyricists)
        daoCollection.composerDao.insertAllComposers(composers)
        daoCollection.genreDao.insertAllGenres(genres)
        daoCollection.songDao.insertAllSongs(songs)
        _scanStatus.send(ScanStatus.ScanComplete)
    }
}