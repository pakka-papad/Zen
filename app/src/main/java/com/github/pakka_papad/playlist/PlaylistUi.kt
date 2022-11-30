package com.github.pakka_papad.playlist

import com.github.pakka_papad.data.music.Song

data class PlaylistUi(
    val songs: List<Song> = listOf(),
    val topBarTitle: String = "",
    val topBarBackgroundImageUri: String = "",
)
