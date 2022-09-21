package tech.zemn.mobile.data

import android.app.Application
import android.provider.MediaStore.Audio
import tech.zemn.mobile.data.music.MetadataExtractor
import tech.zemn.mobile.data.music.Song
import timber.log.Timber
import java.io.File

class DataManager(
    private val context: Application,
) {

    val allSongs = mutableListOf<Song>()

    val albumMap = mutableMapOf<String,ArrayList<Song>>()

    val artistMap = mutableMapOf<String,ArrayList<Song>>()

    fun scanForMusic() {
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
            do {
                try {
                    val file = File(cursor.getString(0))
                    if (file.exists()) {
                        val song = Song(
                            location = file.path,
                            title = cursor.getString(1),
                            album = cursor.getString(2),
                            size = cursor.getFloat(3),
                            addedTimestamp = cursor.getString(4),
                            modifiedTimestamp = cursor.getString(5),
                            metadata = mExtractor.getSongMetadata(file.path)
                        )
                        allSongs.add(song)
                        if (song.album == null || !albumMap.containsKey(song.album)){
                            albumMap[song.album ?: "default"] = ArrayList()
                        }
                        albumMap[song.album ?: "default"]!!.add(song)
                        if (song.metadata.artist == null || !artistMap.containsKey(song.metadata.artist)){
                            artistMap[song.metadata.artist ?: "Unknown"] = ArrayList()
                        }
                        artistMap[song.metadata.artist ?: "Unknown"]!!.add(song)
                        Timber.d(song.toString())
                    }
                } catch (e: Exception) {
                    Timber.e(e.message ?: e.localizedMessage ?: "FILE_DOES_NOT_EXIST")
                }
            } while (cursor.moveToNext())
        }
        cursor?.close()
    }

}