package com.github.pakka_papad.data.music

import androidx.room.Embedded
import androidx.room.Relation

data class GenreWithSongs(
    @Embedded
    val genre: Genre,
    @Relation(
        parentColumn = "genre",
        entityColumn = "genre"
    )
    val songs: List<Song>
)
