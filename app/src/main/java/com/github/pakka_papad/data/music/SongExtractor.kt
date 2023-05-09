package com.github.pakka_papad.data.music

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.github.pakka_papad.formatToDate
import com.github.pakka_papad.toMS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.io.File
import java.io.FileNotFoundException
import java.util.TreeMap

class SongExtractor(
    private val scope: CoroutineScope,
    private val context: Context,
) {

    private val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATE_ADDED,
        MediaStore.Audio.Media.DATE_MODIFIED,
    )

    suspend fun extract(folderPath: String? = null): List<Song> {
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED,
            null
        ) ?: return emptyList()
        val dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
        val titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
        val sizeIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
        val dateAddedIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
        val dateModifiedIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
        val songIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
        val dSongs = ArrayList<Deferred<Song>>()
        cursor.moveToFirst()
        do {
            try {
                val songPath = cursor.getString(dataIndex)
                val songFile = File(songPath)
                if (!songFile.exists()) throw FileNotFoundException()
                if (folderPath != null && songFile.parentFile?.absolutePath != folderPath) throw Exception()
                val size = cursor.getString(sizeIndex)
                val addedDate = cursor.getString(dateAddedIndex)
                val modifiedDate = cursor.getString(dateModifiedIndex)
                val songId = cursor.getLong(songIdIndex)
                val title = cursor.getString(titleIndex)
                val album = cursor.getString(albumIndex)
                dSongs.add(scope.async {
                    getSong(
                        path = songPath,
                        size = size,
                        addedDate = addedDate,
                        modifiedDate = modifiedDate,
                        songId = songId,
                        title = title,
                        album = album,
                    )
                })
            } catch (_: Exception){

            }
        } while (cursor.moveToNext())
        val songs = dSongs.awaitAll()
        cursor.close()
        return songs
    }

    suspend fun extract(blacklistedSongs: List<Song>): Pair<List<Song>,List<Album>>  {
        val blacklistedSongLocations = blacklistedSongs.map { it.location }.toSet()
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED,
            null
        ) ?: return Pair(emptyList(), emptyList())
        val songCover = Uri.parse("content://media/external/audio/albumart")
        val albumArtMap = TreeMap<String,Long>()
        val dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
        val titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val albumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
        val albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
        val sizeIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
        val dateAddedIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
        val dateModifiedIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
        val songIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
        val dSongs = ArrayList<Deferred<Song>>()
        cursor.moveToFirst()
        do {
            try {
                val songPath = cursor.getString(dataIndex)
                val songFile = File(songPath)
                if (!songFile.exists()) throw FileNotFoundException()
                if (blacklistedSongLocations.contains(songFile.path)) continue
                val size = cursor.getString(sizeIndex)
                val addedDate = cursor.getString(dateAddedIndex)
                val modifiedDate = cursor.getString(dateModifiedIndex)
                val songId = cursor.getLong(songIdIndex)
                val title = cursor.getString(titleIndex)
                val album = cursor.getString(albumIndex)
                albumArtMap[album] = cursor.getLong(albumIdIndex)
                dSongs.add(scope.async {
                    getSong(
                        path = songPath,
                        size = size,
                        addedDate = addedDate,
                        modifiedDate = modifiedDate,
                        songId = songId,
                        title = title,
                        album = album,
                    )
                })
            } catch (_: Exception){

            }
        } while (cursor.moveToNext())
        val songs = dSongs.awaitAll()
        cursor.close()
        val albums = albumArtMap.map { (t, u) -> Album(t, ContentUris.withAppendedId(songCover, u).toString()) }
        return Pair(songs,albums)
    }

    companion object {
        private const val UNKNOWN = "Unknown"
    }

    private fun getSong(
        path: String,
        size: String,
        addedDate: String,
        modifiedDate: String,
        songId: Long,
        title: String,
        album: String,
    ): Song {
        val extractor = MediaMetadataRetriever()
        extractor.setDataSource(path)
        val durationMillis = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0
        val sampleRate = if (Build.VERSION.SDK_INT >= 31){
            extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)?.toFloatOrNull() ?: 0f
        } else 0f
        val bitsPerSample = if (Build.VERSION.SDK_INT >= 31){
            extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE)?.toIntOrNull() ?: 0
        } else 0
        val song = Song(
            location = path,
            title = title,
            album = album,
            size = size,
            addedDate = addedDate.toLong().formatToDate(),
            modifiedDate = modifiedDate.toLong().formatToDate(),
            artist = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: UNKNOWN,
            albumArtist = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) ?: UNKNOWN,
            composer = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER) ?: UNKNOWN,
            genre = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: UNKNOWN,
            lyricist = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER) ?: UNKNOWN,
            year = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)?.toIntOrNull() ?: 0,
            comment = null,
            durationMillis = durationMillis,
            durationFormatted = durationMillis.toMS(),
            bitrate = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toFloatOrNull() ?: 0f,
            sampleRate = sampleRate,
            bitsPerSample = bitsPerSample,
            mimeType = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE),
            favourite = false,
            artUri = "content://media/external/audio/media/$songId/albumart"
        )
        extractor.release()
        return song
    }
}