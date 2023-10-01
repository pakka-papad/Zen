package com.github.pakka_papad.data.analytics

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface PlayHistoryDao {

    @Insert
    suspend fun addRecord(history: PlayHistory)


}