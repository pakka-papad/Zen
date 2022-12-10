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

}