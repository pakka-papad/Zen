package com.github.pakka_papad.data.music

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["playlistId","location"])
data class PlaylistSongCrossRef(
    val playlistId: Long,

    // refers to location of song
    @ColumnInfo(index = true)
    val location: String,
)
