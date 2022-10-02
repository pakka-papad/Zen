package tech.zemn.mobile.data.music

import androidx.room.Embedded
import androidx.room.Relation

data class AlbumWithSongs(
    @Embedded
    val album: Album,
    @Relation(
        parentColumn = "name",
        entityColumn = "album"
    )
    val songs: List<Song>,
)
