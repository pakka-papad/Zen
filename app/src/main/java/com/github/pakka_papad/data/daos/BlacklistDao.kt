package com.github.pakka_papad.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.BlacklistedSong
import kotlinx.coroutines.flow.Flow

@Dao
interface BlacklistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSong(blacklistedSong: BlacklistedSong)

    @Query("SELECT * FROM ${Constants.Tables.BLACKLIST_TABLE}")
    suspend fun getBlacklistedSongs(): List<BlacklistedSong>

    @Query("SELECT * FROM ${Constants.Tables.BLACKLIST_TABLE}")
    fun getBlacklistedSongsFlow(): Flow<List<BlacklistedSong>>

    @Delete(entity = BlacklistedSong::class)
    suspend fun deleteBlacklistedSong(blacklistedSong: BlacklistedSong)
}