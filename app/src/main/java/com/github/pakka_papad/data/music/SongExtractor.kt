package com.github.pakka_papad.data.music

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
//import com.github.pakka_papad.data.ZenCrashReporter
import com.github.pakka_papad.data.daos.AlbumArtistDao
import com.github.pakka_papad.data.daos.AlbumDao
import com.github.pakka_papad.data.daos.ArtistDao
import com.github.pakka_papad.data.daos.BlacklistDao
import com.github.pakka_papad.data.daos.BlacklistedFolderDao
import com.github.pakka_papad.data.daos.ComposerDao
import com.github.pakka_papad.data.daos.GenreDao
import com.github.pakka_papad.data.daos.LyricistDao
import com.github.pakka_papad.data.daos.SongDao
import com.github.pakka_papad.formatToDate
import com.github.pakka_papad.toMBfromB
import com.github.pakka_papad.toMS
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.util.TreeMap
import java.util.concurrent.atomic.AtomicInteger

class SongExtractor(
    private val scope: CoroutineScope,
    private val context: Context,
    //private val crashReporter: ZenCrashReporter,
    private val songDao: SongDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val albumArtistDao: AlbumArtistDao,
    private val composerDao: ComposerDao,
    private val lyricistDao: LyricistDao,
    private val genreDao: GenreDao,
    private val blacklistDao: BlacklistDao,
    private val blacklistedFolderDao: BlacklistedFolderDao,
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

    fun resolveSong(location: String): Song? {
        val selection = MediaStore.Audio.Media.DATA + " LIKE ?"
        val selectionArgs = arrayOf(location)
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            MediaStore.Audio.Media.DATE_ADDED,
            null
        ) ?: return null
        val dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
        val titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
        val sizeIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
        val dateAddedIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
        val dateModifiedIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
        val songIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
        var resSong: Song? = null
        cursor.moveToFirst()
        try {
            val songPath = cursor.getString(dataIndex)
            val songFile = File(songPath)
            if (!songFile.exists()) throw FileNotFoundException()
            val size = cursor.getString(sizeIndex)
            val addedDate = cursor.getString(dateAddedIndex)
            val modifiedDate = cursor.getString(dateModifiedIndex)
            val songId = cursor.getLong(songIdIndex)
            val title = cursor.getString(titleIndex).trim()
            val album = cursor.getString(albumIndex).trim()
            resSong = getSong(
                path = songPath,
                size = size,
                addedDate = addedDate,
                modifiedDate = modifiedDate,
                songId = songId,
                title = title,
                album = album,
            )
        } catch (_: Exception){

        }
        cursor.close()
        return resSong
    }

    suspend fun extract(folderPath: String? = null): List<Song> {
        val selection = MediaStore.Audio.Media.DATA + " LIKE ?"
        val selectionArgs = folderPath?.let {
            arrayOf("$it%")
        }
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
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
        val dSongs = ArrayList<Deferred<Song?>>()
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
                val title = cursor.getString(titleIndex).trim()
                val album = cursor.getString(albumIndex).trim()
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
        val songs = dSongs.awaitAll().filterNotNull()
        cursor.close()
        return songs
    }

    fun extractMini(folderPath: String? = null): List<MiniSong> {
        val projectionForMini = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
        )
        val selection = MediaStore.Audio.Media.DATA + " LIKE ?"
        val selectionArgs = folderPath?.let {
            arrayOf("$it%")
        }
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projectionForMini,
            selection,
            selectionArgs,
            MediaStore.Audio.Media.DATE_ADDED,
            null
        ) ?: return emptyList()
        val dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
        val titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
        val songIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
        val songs = ArrayList<MiniSong>()
        cursor.moveToFirst()
        do {
            try {
                val songPath = cursor.getString(dataIndex)
                val songFile = File(songPath)
                if (!songFile.exists()) throw FileNotFoundException()
                if (folderPath != null && songFile.parentFile?.absolutePath != folderPath) throw Exception()
                songs.add(
                    MiniSong(
                        location = songPath,
                        title = cursor.getString(titleIndex).trim(),
                        artUri = "content://media/external/audio/media/${cursor.getLong(songIdIndex)}/albumart",
                        artist = cursor.getString(artistIndex)
                    )
                )
            } catch (_: Exception){

            }
        } while (cursor.moveToNext())
        cursor.close()
        return songs
    }

    private val _scanStatus = Channel<ScanStatus>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val scanStatus = _scanStatus.receiveAsFlow()

    fun scanForMusic() {
        scope.launch {
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
            val (songs, albums) = extract(
                blacklistedSongLocations,
                blacklistedFolderPaths,
                statusListener = { parsed, total ->
                    _scanStatus.trySend(ScanStatus.ScanProgress(parsed, total))
                }
            )
            val insertJobs = listOf(
                launch {
                    val artists = songs.map { it.artist }.toSet().map { Artist(it) }
                    artistDao.insertAllArtists(artists)
                },
                launch {
                    val albumArtists = songs.map { it.albumArtist }.toSet().map { AlbumArtist(it) }
                    albumArtistDao.insertAllAlbumArtists(albumArtists)
                },
                launch {
                    val lyricists = songs.map { it.lyricist }.toSet().map { Lyricist(it) }
                    lyricistDao.insertAllLyricists(lyricists)
                },
                launch {
                    val composers = songs.map { it.composer }.toSet().map { Composer(it) }
                    composerDao.insertAllComposers(composers)
                },
                launch {
                    val genres = songs.map { it.genre }.toSet().map { Genre(it) }
                    genreDao.insertAllGenres(genres)
                }
            )
            albumDao.insertAllAlbums(albums)
            insertJobs.joinAll()
            songDao.insertAllSongs(songs)
            _scanStatus.send(ScanStatus.ScanComplete)
        }
    }

    private suspend fun extract(
        blacklistedSongLocations: HashSet<String>,
        blacklistedFolderPaths: HashSet<String>,
        statusListener: ((parsed: Int, total: Int) -> Unit)? = null
    ): Pair<List<Song>,List<Album>>  {
        val selection = StringBuilder()
        val selectionArgs = arrayListOf<String>()
        selection.append(MediaStore.Audio.Media.IS_MUSIC + " != 0 ")
        blacklistedFolderPaths.forEachIndexed { index, path ->
            selection.append(" AND NOT ")
                .append(MediaStore.Audio.Media.DATA)
                .append(" LIKE ?")
            selectionArgs.add("$path%")
        }
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection.toString(),
            selectionArgs.toTypedArray(),
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
        val dSongs = ArrayList<Deferred<Song?>>()
        val total  = cursor.count
        val parsed = AtomicInteger(0)
        val parseCompletionHandler = object : CompletionHandler {
            override fun invoke(cause: Throwable?) {
                statusListener?.invoke(parsed.incrementAndGet(), total)
            }
        }
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
                val title = cursor.getString(titleIndex).trim()
                val album = cursor.getString(albumIndex).trim()
                albumArtMap[album] = cursor.getLong(albumIdIndex)
                dSongs.add(
                    scope.async {
                        getSong(
                            path = songPath,
                            size = size,
                            addedDate = addedDate,
                            modifiedDate = modifiedDate,
                            songId = songId,
                            title = title,
                            album = album,
                        )
                    }.apply {
                        invokeOnCompletion(parseCompletionHandler)
                    }
                )
            } catch (_: Exception){

            }
        } while (cursor.moveToNext())
        val songs = dSongs.awaitAll().filterNotNull()
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
    ): Song? {
        val extractor = MediaMetadataRetriever()
        var result: Song? = null
        try {
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
                size = size.toFloat().toMBfromB(),
                addedDate = addedDate.toLong().formatToDate(),
                modifiedDate = modifiedDate.toLong().formatToDate(),
                artist = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)?.trim() ?: UNKNOWN,
                albumArtist = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)?.trim() ?: UNKNOWN,
                composer = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER)?.trim() ?: UNKNOWN,
                genre = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)?.trim() ?: UNKNOWN,
                lyricist = extractor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER)?.trim() ?: UNKNOWN,
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
            result = song
        } catch (e: Exception) {
            //crashReporter.logException(e)
            result = null
        } finally {
            try {
                extractor.release()
            } catch (_: Exception) {  }
        }
        return result
    }


}