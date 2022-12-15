package com.github.pakka_papad.data.daos

import androidx.room.Dao
import androidx.room.Insert
import com.github.pakka_papad.data.music.BlacklistedSong

@Dao
interface BlacklistDao {

    @Insert
    suspend fun addSong(blacklistedSong: BlacklistedSong)

}