package tech.zemn.mobile.data

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore.Audio
import tech.zemn.mobile.data.music.Album
import tech.zemn.mobile.data.music.MetadataExtractor
import tech.zemn.mobile.data.music.Song
import tech.zemn.mobile.data.music.SongDao
import tech.zemn.mobile.data.notification.ZemnNotificationManager
import tech.zemn.mobile.player.ZemnPlayer
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException

class DataManager(
    private val context: Context,
    private val notificationManager: ZemnNotificationManager,
    private val songDao: SongDao
) {

    val allSongs = songDao.getAllSongs()
    val allAlbums = songDao.getAllAlbumsWithSongs()

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
        do {
            try {
                val file = File(cursor.getString(dataIndex))
                if (!file.exists()) throw FileNotFoundException()
                val songMetadata = mExtractor.getSongMetadata(file.path)
                val song = Song(
                    location = file.path,
                    title = cursor.getString(titleIndex),
                    album = cursor.getString(albumIndex).trim(),
                    size = cursor.getFloat(sizeIndex),
                    addedTimestamp = cursor.getString(dateAddedIndex).toLong(),
                    modifiedTimestamp = cursor.getString(dateModifiedIndex).toLong(),
                    artist = songMetadata.artist,
                    albumArtist = songMetadata.albumArtist,
                    composer = songMetadata.composer,
                    genre = songMetadata.genre,
                    lyricist = songMetadata.lyricist,
                    year = songMetadata.year,
                    comment = songMetadata.comment,
                    duration = songMetadata.duration,
                    bitrate = songMetadata.bitrate,
                    sampleRate = songMetadata.sampleRate,
                    bitsPerSample = songMetadata.bitsPerSample,
                    mimeType = songMetadata.mimeType,
                )
                songs.add(song)
                if (!albumArtMap.containsKey(song.album)) {
                    albumArtMap[song.album] = null
                }
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
        notificationManager.removeScanningNotification()
    }

    private var callback: Callback? = null
    private val queue = ArrayList<Song>()

    init {
        if (callback == null) {
            val intent = Intent(context, ZemnPlayer::class.java)
            context.startForegroundService(intent)
        }
    }

    @Synchronized
    fun updateQueue(newQueue: List<Song>) {
        queue.clear()
        queue.addAll(newQueue)
        callback?.updateQueue(newQueue)
    }

    fun setPlayerRunning(callback: Callback) {
        this.callback = callback
        this.callback?.updateQueue(queue)
    }

    fun stopPlayerRunning() {
        this.callback = null
    }

    interface Callback {
        fun updateQueue(newQueue: List<Song>)
    }
}