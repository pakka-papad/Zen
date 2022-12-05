package com.github.pakka_papad.collection

import com.github.pakka_papad.data.music.Song

data class CollectionUi(
    val error: String? = null,
    val songs: List<Song> = listOf(),
    val topBarTitle: String = "",
    val topBarBackgroundImageUri: String = "",
)
