package com.github.pakka_papad.data.music

import androidx.room.Embedded
import androidx.room.Relation

data class ComposerWithSongs(
    @Embedded
    val composer: Composer,
    @Relation(
        parentColumn = "name",
        entityColumn = "composer"
    )
    val songs: List<Song>,
)
