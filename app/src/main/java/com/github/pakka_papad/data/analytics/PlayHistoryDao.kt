package com.github.pakka_papad.data.analytics

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.Song

@Dao
interface PlayHistoryDao {

    @Query(
        "SELECT * FROM ${Constants.Tables.SONG_TABLE} WHERE location = :location"
    )
    suspend fun getSongFromLocation(location: String): Song?

    @Update
    suspend fun updateSong(song: Song)

    @Insert
    suspend fun insertRecord(record: PlayHistory)

    @Transaction
    suspend fun addRecord(location: String) {
        val song = getSongFromLocation(location) ?: return
        val time = System.currentTimeMillis()
        val updatedSong = song.copy(playCount = 1 + song.playCount, lastPlayed = time)
        val record = PlayHistory(location, time)
        updateSong(updatedSong)
        insertRecord(record)
    }


}