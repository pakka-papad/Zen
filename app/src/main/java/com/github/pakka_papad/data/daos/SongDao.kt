package com.github.pakka_papad.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.AlbumArtistWithSongCount
import com.github.pakka_papad.data.music.ArtistWithSongCount
import com.github.pakka_papad.data.music.ComposerWithSongCount
import com.github.pakka_papad.data.music.GenreWithSongCount
import com.github.pakka_papad.data.music.LyricistWithSongCount
import com.github.pakka_papad.data.music.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllSongs(data: List<Song>)

    @Query("SELECT * FROM ${Constants.Tables.SONG_TABLE} ORDER BY title ASC")
    fun getAllSongs(): Flow<List<Song>>

    @Query("SELECT * FROM ${Constants.Tables.SONG_TABLE}")
    suspend fun getSongs(): List<Song>

    @Update
    suspend fun updateSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Query("DELETE FROM ${Constants.Tables.SONG_TABLE}")
    suspend fun deleteAllSongs()

    @Transaction
    @Query("DELETE FROM ${Constants.Tables.SONG_TABLE} WHERE location LIKE :prefix || '%'")
    suspend fun deleteSongsWithPathPrefix(prefix: String)

    @Query("SELECT * FROM ${Constants.Tables.SONG_TABLE} WHERE title LIKE '%' || :query || '%' OR " +
            "artist LIKE '%' || :query || '%' OR " +
            "albumArtist LIKE '%' || :query || '%' OR " +
            "composer LIKE '%' || :query || '%' OR " +
            "genre LIKE '%' || :query || '%' OR " +
            "lyricist LIKE '%' || :query || '%'")
    suspend fun searchSongs(query: String): List<Song>

    @Query("SELECT artist as name, COUNT(*) as count FROM ${Constants.Tables.SONG_TABLE} GROUP BY " +
            "${Constants.Tables.SONG_TABLE}.artist")
    fun getAllArtistsWithSongCount(): Flow<List<ArtistWithSongCount>>

    @Query("SELECT albumArtist as name, COUNT(*) as count FROM ${Constants.Tables.SONG_TABLE} GROUP BY " +
            "${Constants.Tables.SONG_TABLE}.albumArtist")
    fun getAllAlbumArtistsWithSongCount(): Flow<List<AlbumArtistWithSongCount>>


    @Query("SELECT composer as name, COUNT(*) as count FROM ${Constants.Tables.SONG_TABLE} GROUP BY " +
            "${Constants.Tables.SONG_TABLE}.composer")
    fun getAllComposersWithSongCount(): Flow<List<ComposerWithSongCount>>

    @Query("SELECT lyricist as name, COUNT(*) as count FROM ${Constants.Tables.SONG_TABLE} GROUP BY " +
            "${Constants.Tables.SONG_TABLE}.lyricist")
    fun getAllLyricistsWithSongCount(): Flow<List<LyricistWithSongCount>>

    @Query("SELECT genre AS genreName, COUNT(*) AS count FROM ${Constants.Tables.SONG_TABLE} GROUP BY " +
            "${Constants.Tables.SONG_TABLE}.genre")
    fun getAllGenresWithSongCount(): Flow<List<GenreWithSongCount>>

    @Query("SELECT * FROM ${Constants.Tables.SONG_TABLE} WHERE favourite = 1")
    fun getAllFavourites(): Flow<List<Song>>

    @Query("SELECT * FROM ${Constants.Tables.SONG_TABLE} WHERE location IN (:locations)")
    suspend fun getSongsFromLocations(locations: List<String>): List<Song>
}