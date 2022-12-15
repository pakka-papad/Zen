package com.github.pakka_papad.data.music

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.pakka_papad.Constants

@Entity(tableName = Constants.Tables.BLACKLIST_TABLE)
data class BlacklistedSong(
    @PrimaryKey val location: String,
    val title: String,
    val artist: String,
)
