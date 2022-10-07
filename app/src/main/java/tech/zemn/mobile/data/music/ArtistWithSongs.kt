package tech.zemn.mobile.data.music

import androidx.room.Embedded
import androidx.room.Relation

data class ArtistWithSongs(
    @Embedded
    val artist: Artist,
    @Relation(
        parentColumn = "name",
        entityColumn = "artist"
    )
    val songs: List<Song>,
)
