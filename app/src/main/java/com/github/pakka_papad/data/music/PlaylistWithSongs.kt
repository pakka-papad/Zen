package com.github.pakka_papad.data.music

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithSongs(
    @Embedded
    val playlist: Playlist,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "location",
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val songs: List<Song>
)
