package com.github.pakka_papad.data

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.Audio
import android.widget.Toast
import com.github.pakka_papad.data.music.*
import com.github.pakka_papad.data.notification.ZenNotificationManager
import com.github.pakka_papad.formatToDate
import com.github.pakka_papad.player.ZenPlayer
import com.github.pakka_papad.toMBfromB
import com.github.pakka_papad.toMinutesAndSeconds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
    val allAlbums = songDao.getAllAlbumsWithSongs()
    val allArtists = songDao.getAllArtistsWithSongs()

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
        val songs = ArrayList<Song>()
        val albums = ArrayList<Album>()
        val dataIndex = cursor.getColumnIndex(Audio.Media.DATA)
        val titleIndex = cursor.getColumnIndex(Audio.Media.TITLE)
        val albumIdIndex = cursor.getColumnIndex(Audio.Media.ALBUM_ID)
        val albumIndex = cursor.getColumnIndex(Audio.Media.ALBUM)
        val sizeIndex = cursor.getColumnIndex(Audio.Media.SIZE)
        val dateAddedIndex = cursor.getColumnIndex(Audio.Media.DATE_ADDED)
        val dateModifiedIndex = cursor.getColumnIndex(Audio.Media.DATE_MODIFIED)
        val songCover = Uri.parse("content://media/external/audio/albumart")
        val albumArtMap = HashMap<String,String?>()
        val artistSet = TreeSet<String>()
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
                    albumArtist = songMetadata.albumArtist,
                    composer = songMetadata.composer,
                    genre = songMetadata.genre,
                    lyricist = songMetadata.lyricist,
                    year = songMetadata.year,
                    comment = songMetadata.comment,
                    durationMillis = songMetadata.duration,
                    durationFormatted = songMetadata.duration.toMinutesAndSeconds(),
                    bitrate = songMetadata.bitrate,
                    sampleRate = songMetadata.sampleRate,
                    bitsPerSample = songMetadata.bitsPerSample,
                    mimeType = songMetadata.mimeType,
                )
                songs.add(song)
                artistSet.add(song.artist)
                if (albumArtMap[song.album] == null){
                    albumArtMap[song.album] = ContentUris.withAppendedId(songCover, cursor.getLong(albumIdIndex)).toString()
                }
            } catch (e: Exception) {
                Timber.e(e.message ?: e.localizedMessage ?: "FILE_DOES_NOT_EXIST")
            }
            parsedSongs++
            _scanStatus.send(ScanStatus.ScanProgress(parsedSongs,totalSongs))
        } while (cursor.moveToNext())
        cursor.close()
        albumArtMap.forEach { (albumName, albumArtUri) ->
            albums.add(Album(name = albumName, albumArtUri = albumArtUri))
        }
        songDao.insertAllSongs(songs)
        songDao.insertAllAlbums(albums)
        songDao.insertAllArtists(artistSet.map { Artist(it) })
        notificationManager.removeScanningNotification()
        _scanStatus.send(ScanStatus.ScanComplete)
    }

    private fun showToast(message: String) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

    private var callback: Callback? = null

    private val _queue = MutableStateFlow(listOf<Song>())
    val queue = _queue.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()

    suspend fun updateSong(song: Song) {
        if (_currentSong.value?.location == song.location){
           _currentSong.value = song
        }
        if (_queue.value.any { it.location == song.location }){
            _queue.value = _queue.value.map {
                if (it.location == song.location) song else it
            }
        }
        songDao.updateSong(song)
    }

    private var remIdx = 0

    @Synchronized
    fun setQueue(newQueue: List<Song>, startPlayingFromIndex: Int) {
        if (newQueue.isEmpty()) return
        _queue.value = newQueue
        _currentSong.value = newQueue[startPlayingFromIndex]
        if (callback == null){
            val intent = Intent(context,ZenPlayer::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            remIdx = startPlayingFromIndex
        } else {
            callback?.setQueue(newQueue,startPlayingFromIndex)
        }
    }

    @Synchronized
    fun addToQueue(song: Song) {
        if (_queue.value.any { it.location == song.location }) {
            showToast("Song already in queue")
            return
        }
        _queue.value = _queue.value.toMutableList().apply { add(song) }
        callback?.addToQueue(song)
    }

    fun setPlayerRunning(callback: Callback) {
        this.callback = callback
        this.callback?.setQueue(_queue.value,remIdx)
    }

    fun updateCurrentSong(currentSongIndex: Int) {
        if (currentSongIndex < 0 || currentSongIndex >= _queue.value.size) return
        _currentSong.value = _queue.value[currentSongIndex]
    }

    fun getSongAtIndex(index: Int): Song? {
        if (index < 0 || index >= _queue.value.size) return null
        return _queue.value[index]
    }

    fun stopPlayerRunning() {
        this.callback = null
    }

    interface Callback {
        fun setQueue(newQueue: List<Song>, startPlayingFromIndex: Int)
        fun addToQueue(song: Song)
    }
}