package com.github.pakka_papad.data.daos

import androidx.room.*
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllSongs(data: List<Song>)

    @Query("SELECT * FROM ${Constants.Tables.SONG_TABLE} ORDER BY title ASC")
    fun getAllSongs(): Flow<List<Song>>

    @Update
    suspend fun updateSong(song: Song)

    @Query("DELETE FROM ${Constants.Tables.SONG_TABLE}")
    suspend fun deleteAllSongs()

    @Query("SELECT * FROM ${Constants.Tables.SONG_TABLE} WHERE title LIKE '%' || :query || '%' OR " +
            "artist LIKE '%' || :query || '%' OR " +
            "albumArtist LIKE '%' || :query || '%' OR " +
            "composer LIKE '%' || :query || '%' OR " +
            "genre LIKE '%' || :query || '%' OR " +
            "lyricist LIKE '%' || :query || '%'")
    suspend fun searchSongs(query: String): List<Song>

}