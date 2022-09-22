package tech.zemn.mobile.data

import android.content.Context
import android.provider.MediaStore.Audio
import tech.zemn.mobile.data.music.MetadataExtractor
import tech.zemn.mobile.data.music.Song
import tech.zemn.mobile.data.music.SongDao
import tech.zemn.mobile.data.notification.ZemnNotificationManager
import timber.log.Timber
import java.io.File

class DataManager(
    private val context: Context,
    private val notificationManager: ZemnNotificationManager,
    private val songDao: SongDao
) {

    val allSongs = songDao.getAllSongs()

    suspend fun scanForMusic() {
        notificationManager.sendScanningNotification()
        val selection = Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            Audio.Media.DATA,
            Audio.Media.TITLE,
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
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val mExtractor = MetadataExtractor()
            val songs = ArrayList<Song>()
            do {
                try {
                    val file = File(cursor.getString(0))
                    if (file.exists()) {
                        val songMetadata = mExtractor.getSongMetadata(file.path)
                        val song = Song(
                            location = file.path,
                            title = cursor.getString(1),
                            album = cursor.getString(2),
                            size = cursor.getFloat(3),
                            addedTimestamp = cursor.getString(4).toLong(),
                            modifiedTimestamp = cursor.getString(5).toLong(),
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
                        Timber.d(song.toString())
                    }
                } catch (e: Exception) {
                    Timber.e(e.message ?: e.localizedMessage ?: "FILE_DOES_NOT_EXIST")
                }
            } while (cursor.moveToNext())
            songDao.insertAllSongs(songs)
        }
        cursor?.close()
        notificationManager.removeScanningNotification()
    }

}