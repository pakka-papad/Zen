package com.github.pakka_papad.data.daos

import androidx.room.*
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.Lyricist
import com.github.pakka_papad.data.music.LyricistWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface LyricistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllLyricists(data: List<Lyricist>)

    @Query("SELECT * FROM ${Constants.Tables.LYRICIST_TABLE} WHERE name LIKE '%' || :query || '%'")
    suspend fun searchLyricists(query: String): List<Lyricist>

    @Transaction
    @Query("SELECT * FROM ${Constants.Tables.LYRICIST_TABLE} WHERE name = :name")
    fun getLyricistWithSongs(name: String): Flow<LyricistWithSongs?>

}