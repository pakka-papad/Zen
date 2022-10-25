package tech.zemn.mobile.data

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore.Audio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tech.zemn.mobile.data.music.*
import tech.zemn.mobile.data.notification.ZemnNotificationManager
import tech.zemn.mobile.formatToDate
import tech.zemn.mobile.player.ZemnPlayer
import tech.zemn.mobile.toMBfromB
import tech.zemn.mobile.toMinutesAndSeconds
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.util.TreeSet

class DataManager(
    private val context: Context,
    private val notificationManager: ZemnNotificationManager,
    private val songDao: SongDao
) {

    val allSongs = songDao.getAllSongs()
    val allAlbums = songDao.getAllAlbumsWithSongs()
    val allArtists = songDao.getAllArtistsWithSongs()

    suspend fun scanForMusic() {
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
        } while (cursor.moveToNext())
        cursor.close()
        albumArtMap.forEach { (albumName, albumArtUri) ->
            albums.add(Album(name = albumName, albumArtUri = albumArtUri))
        }
        songDao.insertAllSongs(songs)
        songDao.insertAllAlbums(albums)
        songDao.insertAllArtists(artistSet.map { Artist(it) })
        notificationManager.removeScanningNotification()
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
        _queue.value = _queue.value.toMutableList().apply {
            clear()
            addAll(newQueue)
        }
        _currentSong.value = newQueue[startPlayingFromIndex]
        if (callback == null){
            val intent = Intent(context,ZemnPlayer::class.java)
            context.startForegroundService(intent)
            remIdx = startPlayingFromIndex
        } else {
            callback?.setQueue(newQueue,startPlayingFromIndex)
        }
    }

    @Synchronized
    fun addToQueue(song: Song) {
        _queue.value.toMutableList().apply { add(song) }
        callback?.addToQueue(song)
    }

    fun setPlayerRunning(callback: Callback) {
        this.callback = callback
        this.callback?.setQueue(_queue.value,remIdx)
    }

    fun updateCurrentSong(song: Song) {
        _currentSong.value = song
    }

    fun stopPlayerRunning() {
        this.callback = null
    }

    interface Callback {
        fun setQueue(newQueue: List<Song>, startPlayingFromIndex: Int)
        fun addToQueue(song: Song)
    }
}