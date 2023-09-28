package com.github.pakka_papad.data

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import com.github.pakka_papad.data.components.*
import com.github.pakka_papad.data.music.*
import com.github.pakka_papad.data.notification.ZenNotificationManager
import com.github.pakka_papad.nowplaying.RepeatMode
import com.github.pakka_papad.player.ZenPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.io.File
import kotlin.collections.HashSet

class DataManager(
    private val context: Context,
    private val notificationManager: ZenNotificationManager,
    private val daoCollection: DaoCollection,
    private val scope: CoroutineScope,
    private val songExtractor: SongExtractor,
) {

    val getAll by lazy { GetAll(daoCollection) }

    val findCollection by lazy { FindCollection(daoCollection) }

    val querySearch by lazy { QuerySearch(daoCollection) }

    val blacklistedSongLocations = HashSet<String>()
    val blacklistedFolderPaths = HashSet<String>()

    init {
        cleanData()
        buildBlacklistStore()
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

    private fun buildBlacklistStore(){
        scope.launch {
            val blacklistedSongs = daoCollection.blacklistDao.getBlacklistedSongs()
            blacklistedSongs.forEach { blacklistedSongLocations.add(it.location) }
            val blacklistedFolders = daoCollection.blacklistedFolderDao.getAllFolders().first()
            blacklistedFolders.forEach { blacklistedFolderPaths.add(it.path) }
        }
    }

    suspend fun removeFromBlacklist(data: List<BlacklistedSong>){
        data.forEach {
            Timber.d("bs: $it")
            daoCollection.blacklistDao.deleteBlacklistedSong(it)
            blacklistedSongLocations.remove(it.location)
        }
    }

    suspend fun addFolderToBlacklist(path: String){
        daoCollection.songDao.deleteSongsWithPathPrefix(path)
        daoCollection.blacklistedFolderDao.insertFolder(BlacklistedFolder(path))
        blacklistedFolderPaths.add(path)
        cleanData()
    }

    suspend fun removeFoldersFromBlacklist(folders: List<BlacklistedFolder>){
        folders.forEach { folder ->
            try {
                daoCollection.blacklistedFolderDao.deleteFolder(folder)
                blacklistedFolderPaths.remove(folder.path)
            } catch (_: Exception){ }
        }
    }

    suspend fun createPlaylist(playlistName: String) {
        if (playlistName.trim().isEmpty()) return
        val playlist = PlaylistExceptId(
            playlistName = playlistName.trim(),
            createdAt = System.currentTimeMillis()
        )
        daoCollection.playlistDao.insertPlaylist(playlist)
        showToast("Playlist $playlistName created")
    }

    suspend fun deletePlaylist(playlist: Playlist) = daoCollection.playlistDao.deletePlaylist(playlist)
    suspend fun deleteSong(song: Song) {
        daoCollection.songDao.deleteSong(song)
        daoCollection.blacklistDao.addSong(
            BlacklistedSong(
                location = song.location,
                title = song.title,
                artist = song.artist,
            )
        )
        blacklistedSongLocations.add(song.location)
    }

    suspend fun insertPlaylistSongCrossRefs(playlistSongCrossRefs: List<PlaylistSongCrossRef>) =
        daoCollection.playlistDao.insertPlaylistSongCrossRef(playlistSongCrossRefs)

    suspend fun deletePlaylistSongCrossRef(playlistSongCrossRef: PlaylistSongCrossRef) =
        daoCollection.playlistDao.deletePlaylistSongCrossRef(playlistSongCrossRef)

    private val _scanStatus = Channel<ScanStatus>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val scanStatus = _scanStatus.receiveAsFlow()

    fun scanForMusic() = scope.launch {
        _scanStatus.send(ScanStatus.ScanStarted)
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

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private var callback: Callback? = null

    private val _queue = mutableStateListOf<Song>()
    val queue: List<Song> = _queue

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()

    fun moveItem(fromIndex: Int, toIndex: Int) {
        _queue.apply { add(toIndex, removeAt(fromIndex)) }
    }

    suspend fun updateSong(song: Song) {
        if (_currentSong.value?.location == song.location) {
            _currentSong.update { song }
            callback?.updateNotification()
        }
        for (idx in _queue.indices) {
            if (_queue[idx].location == song.location) {
                _queue[idx] = song
                break
            }
        }
        daoCollection.songDao.updateSong(song)
    }

    private val _repeatMode = MutableStateFlow<RepeatMode>(RepeatMode.NO_REPEAT)
    val repeatMode = _repeatMode.asStateFlow()

    fun updateRepeatMode(newRepeatMode: RepeatMode){
        _repeatMode.update { newRepeatMode }
    }

    private var remIdx = 0

    @Synchronized
    fun setQueue(newQueue: List<Song>, startPlayingFromIndex: Int) {
        if (newQueue.isEmpty()) return
        _queue.apply {
            clear()
            addAll(newQueue)
        }
        _currentSong.value = newQueue[startPlayingFromIndex]
        if (callback == null) {
            val intent = Intent(context, ZenPlayer::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            remIdx = startPlayingFromIndex
        } else {
            callback?.setQueue(newQueue, startPlayingFromIndex)
        }
    }

    /**
     * Returns true if added to queue else returns false if already in queue
     */
    @Synchronized
    fun addToQueue(song: Song): Boolean {
        if (_queue.any { it.location == song.location }) return false
        _queue.add(song)
        callback?.addToQueue(song)
        return true
    }

    fun setPlayerRunning(callback: Callback) {
        this.callback = callback
        this.callback?.setQueue(_queue, remIdx)
    }

    fun updateCurrentSong(currentSongIndex: Int) {
        if (currentSongIndex < 0 || currentSongIndex >= _queue.size) return
        _currentSong.update { _queue[currentSongIndex] }
    }

    fun getSongAtIndex(index: Int): Song? {
        if (index < 0 || index >= _queue.size) return null
        return _queue[index]
    }

    fun stopPlayerRunning() {
        this.callback = null
        _currentSong.update { null }
        _queue.clear()
    }

    interface Callback {
        fun setQueue(newQueue: List<Song>, startPlayingFromIndex: Int)
        fun addToQueue(song: Song)
        fun updateNotification()
    }
}