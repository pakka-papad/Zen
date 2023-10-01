package com.github.pakka_papad.data.analytics

import androidx.room.Entity
import androidx.room.ForeignKey
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.Song

@Entity(
    tableName = Constants.Tables.PLAY_HISTORY_TABLE,
    primaryKeys = ["songLocation","timestamp"],
    foreignKeys = [
        ForeignKey(
            entity = Song::class,
            parentColumns = ["location"],
            childColumns = ["songLocation"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlayHistory(
    val songLocation: String,
    val timestamp: Long,
)
