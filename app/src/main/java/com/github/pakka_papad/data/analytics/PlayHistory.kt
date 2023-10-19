package com.github.pakka_papad.data.analytics

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.Song

@Entity(
    tableName = Constants.Tables.PLAY_HISTORY_TABLE,
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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val songLocation: String,
    val timestamp: Long,
    val playDuration: Long,
)
