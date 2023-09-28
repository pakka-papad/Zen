package com.github.pakka_papad.data.daos

import androidx.room.*
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.Genre
import com.github.pakka_papad.data.music.GenreWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllGenres(data: List<Genre>)

    @Query("SELECT * FROM ${Constants.Tables.GENRE_TABLE} WHERE genre LIKE '%' || :query || '%'")
    suspend fun searchGenres(query: String): List<Genre>

    @Transaction
    @Query("SELECT * FROM ${Constants.Tables.GENRE_TABLE} WHERE genre = :genreName")
    fun getGenreWithSongs(genreName: String): Flow<GenreWithSongs?>

    @Query("DELETE FROM ${Constants.Tables.GENRE_TABLE} WHERE genre = :genre")
    suspend fun deleteGenre(genre: String)

    @Transaction
    @Query("DELETE FROM ${Constants.Tables.GENRE_TABLE} WHERE genre IN " +
            "(SELECT genre.genre as genre FROM ${Constants.Tables.GENRE_TABLE} as genre LEFT JOIN " +
            "${Constants.Tables.SONG_TABLE} as song ON genre.genre = song.genre GROUP BY genre.genre " +
            "HAVING COUNT(song.location) = 0)")
    suspend fun cleanGenreTable()
}