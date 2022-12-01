package com.github.pakka_papad.data.music

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.pakka_papad.Constants

@Entity(tableName = Constants.Tables.PLAYLIST_TABLE)
data class Playlist(
    @PrimaryKey(autoGenerate = true) val playlistId: Long,
    val playlistName: String,
    val createdAt: Long,
)

data class PlaylistExceptId(
    val playlistName: String,
    val createdAt: Long,
)
