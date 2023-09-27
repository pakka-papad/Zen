package com.github.pakka_papad.data.daos

import androidx.room.*
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.Composer
import com.github.pakka_papad.data.music.ComposerWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface ComposerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllComposers(data: List<Composer>)

    @Query("SELECT * FROM ${Constants.Tables.COMPOSER_TABLE} WHERE name LIKE '%' || :query || '%'")
    suspend fun searchComposers(query: String): List<Composer>

    @Transaction
    @Query("SELECT * FROM ${Constants.Tables.COMPOSER_TABLE} WHERE name = :name")
    fun getComposerWithSongs(name: String): Flow<ComposerWithSongs?>

    @Transaction
    @Query("DELETE FROM ${Constants.Tables.COMPOSER_TABLE} WHERE name IN " +
            "(SELECT composer.name as name FROM ${Constants.Tables.COMPOSER_TABLE} as composer LEFT JOIN " +
            "${Constants.Tables.SONG_TABLE} as song ON composer.name = song.composer GROUP BY composer.name " +
            "HAVING COUNT(song.location) = 0)")
    suspend fun cleanComposerTable()
}