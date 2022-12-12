package com.github.pakka_papad.data.music

data class PlaylistWithSongCount(
    val playlistId: Long,
    val playlistName: String,
    val createdAt: Long,
    val count: Int = 0,
)
