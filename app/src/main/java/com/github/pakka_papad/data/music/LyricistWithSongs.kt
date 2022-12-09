package com.github.pakka_papad.data.music

import androidx.room.Embedded
import androidx.room.Relation

data class LyricistWithSongs(
    @Embedded
    val lyricist: Lyricist,
    @Relation(
        parentColumn = "name",
        entityColumn = "lyricist"
    )
    val songs: List<Song>,
)
