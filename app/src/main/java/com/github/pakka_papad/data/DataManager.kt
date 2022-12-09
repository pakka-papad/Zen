package com.github.pakka_papad.data

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.Audio
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import com.github.pakka_papad.data.music.*
import com.github.pakka_papad.data.notification.ZenNotificationManager
import com.github.pakka_papad.formatToDate
import com.github.pakka_papad.player.ZenPlayer
import com.github.pakka_papad.toMBfromB
import com.github.pakka_papad.toMS
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class DataManager(
    private val context: Context,
    private val notificationManager: ZenNotificationManager,
    private val songDao: SongDao,
) {

    val allSongs = songDao.getAllSongs()
    val allAlbums = songDao.getAllAlbums()
    val allArtistsWithSongs = songDao.getAllArtistsWithSongs()
    val allPlaylists = songDao.getAllPlaylists()

    fun getPlaylistWithSongsById(id: Long) = songDao.getPlaylistWithSongs(id)
    fun getAlbumWithSongsByName(albumName: String) = songDao.getAlbumWithSongsByName(albumName)
    fun getArtistWithSongsByName(artistName: String) = songDao.getArtistWithSongsByName(artistName)
    fun getAlbumArtistWithSings(albumArtistName: String) = songDao.getAlbumArtistWithSongs(albumArtistName)
    fun getComposerWithSongs(composerName: String) = songDao.getComposerWithSongs(composerName)
    fun getLyricistWithSongs(lyricistName: String) = songDao.getLyricistWithSongs(lyricistName)
    fun getGenreWithSongs(genreName: String) = songDao.getGenreWithSongs(genreName)

    suspend fun searchSongs(query: String) = songDao.searchSongs(query)
    suspend fun searchAlbums(query: String) = songDao.searchAlbums(query)
    suspend fun searchArtists(query: String) = songDao.searchArtists(query)
    suspend fun searchAlbumArtists(query: String) = songDao.searchAlbumArtists(query)
    suspend fun searchComposers(query: String) = songDao.searchComposers(query)
    suspend fun searchLyricists(query: String) = songDao.searchLyricists(query)
    suspend fun searchPlaylists(query: String) = songDao.searchPlaylists(query)
    suspend fun searchGenres(query: String) = songDao.searchGenres(query)

    suspend fun createPlaylist(playlistName: String) {
        if (playlistName.trim().isEmpty()) return
        val playlist = PlaylistExceptId(
            playlistName = playlistName.trim(),
            createdAt = System.currentTimeMillis()
        )
        songDao.insertPlaylist(playlist)
        showToast("Playlist $playlistName created")
    }

    suspend fun insertPlaylistSongCrossRefs(playlistSongCrossRefs: List<PlaylistSongCrossRef>) {
        try {
            songDao.insertPlaylistSongCrossRef(playlistSongCrossRefs)
            showToast("Done")
        } catch (e: Exception) {
            Timber.e(e)
            showToast("Oops! Some error occurred")
        }
    }

    private val _scanStatus = Channel<ScanStatus>()
    val scanStatus = _scanStatus.receiveAsFlow()

    suspend fun scanForMusic() {
        _scanStatus.send(ScanStatus.ScanStarted)
        notificationManager.sendScanningNotification()
        val selection = Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            Audio.Media.DATA,
            Audio.Media.TITLE,
            Audio.Media.ALBUM_ID,
            Audio.Media.ALBUM,
            Audio.Media.SIZE,
            Audio.Media.DATE_ADDED,
            Audio.Media.DATE_MODIFIED,
            Audio.Media._ID
        )
        val cursor = context.contentResolver.query(
            Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            Audio.Media.DATE_ADDED,
            null
        ) ?: return
        val totalSongs = cursor.count
        var parsedSongs = 0
        cursor.moveToFirst()
        val mExtractor = MetadataExtractor()
        val dataIndex = cursor.getColumnIndex(Audio.Media.DATA)
        val titleIndex = cursor.getColumnIndex(Audio.Media.TITLE)
        val albumIdIndex = cursor.getColumnIndex(Audio.Media.ALBUM_ID)
        val albumIndex = cursor.getColumnIndex(Audio.Media.ALBUM)
        val sizeIndex = cursor.getColumnIndex(Audio.Media.SIZE)
        val dateAddedIndex = cursor.getColumnIndex(Audio.Media.DATE_ADDED)
        val dateModifiedIndex = cursor.getColumnIndex(Audio.Media.DATE_MODIFIED)
        val songIdIndex = cursor.getColumnIndex(Audio.Media._ID)
        val songCover = Uri.parse("content://media/external/audio/albumart")

        val songs = ArrayList<Song>()
        val albumArtMap = HashMap<String, String?>()
        val artistSet = TreeSet<String>()
        val albumArtistSet = TreeSet<String>()
        val composerSet = TreeSet<String>()
        val genreSet = TreeSet<String>()
        val lyricistSet = TreeSet<String>()
        do {
            try {
                val file = File(cursor.getString(dataIndex))
                if (!file.exists()) throw FileNotFoundException()
                val songMetadata = mExtractor.getSongMetadata(file.path)
                val song = Song(
                    location = file.path,
                    title = cursor.getString(titleIndex),
                    album = cursor.getString(albumIndex).trim(),
                    size = cursor.getFloat(sizeIndex).toMBfromB(),
                    addedDate = cursor.getString(dateAddedIndex).toLong().formatToDate(),
                    modifiedDate = cursor.getString(dateModifiedIndex).toLong().formatToDate(),
                    artist = songMetadata.artist.trim(),
                    albumArtist = songMetadata.albumArtist.trim(),
                    composer = songMetadata.composer.trim(),
                    genre = songMetadata.genre.trim(),
                    lyricist = songMetadata.lyricist.trim(),
                    year = songMetadata.year,
                    comment = songMetadata.comment,
                    durationMillis = songMetadata.duration,
                    durationFormatted = songMetadata.duration.toMS(),
                    bitrate = songMetadata.bitrate,
                    sampleRate = songMetadata.sampleRate,
                    bitsPerSample = songMetadata.bitsPerSample,
                    mimeType = songMetadata.mimeType,
                    artUri = "content://media/external/audio/media/${cursor.getLong(songIdIndex)}/albumart"
                )
                songs.add(song)
                artistSet.add(song.artist)
                albumArtistSet.add(song.albumArtist)
                composerSet.add(song.composer)
                lyricistSet.add(song.lyricist)
                genreSet.add(song.genre)
                if (albumArtMap[song.album] == null) {
                    albumArtMap[song.album] =
                        ContentUris.withAppendedId(songCover, cursor.getLong(albumIdIndex))
                            .toString()
                }
            } catch (e: Exception) {
                Timber.e(e.message ?: e.localizedMessage ?: "FILE_DOES_NOT_EXIST")
            }
            parsedSongs++
            _scanStatus.send(ScanStatus.ScanProgress(parsedSongs, totalSongs))
        } while (cursor.moveToNext())
        cursor.close()
        songDao.insertAllAlbums(albumArtMap.entries.map { (t,u) -> Album(t,u) })
        songDao.insertAllArtists(artistSet.map { Artist(it) })
        songDao.insertAllAlbumArtists(albumArtistSet.map { AlbumArtist(it) })
        songDao.insertAllComposers(composerSet.map { Composer(it) })
        songDao.insertAllLyricists(lyricistSet.map { Lyricist(it) })
        songDao.insertAllGenres(genreSet.map { Genre(it) })
        songDao.insertAllSongs(songs)
        notificationManager.removeScanningNotification()
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

    suspend fun updateSong(song: Song) {
        if (_currentSong.value?.location == song.location) {
            _currentSong.update { song }
            callback?.updateNotification()
        }
        for (idx in _queue.indices){
            if (_queue[idx].location == song.location){
                _queue[idx] = song
                break
            }
        }
        songDao.updateSong(song)
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