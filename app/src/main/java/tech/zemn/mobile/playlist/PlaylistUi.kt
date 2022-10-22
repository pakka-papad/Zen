package tech.zemn.mobile.playlist

import tech.zemn.mobile.data.music.Song

data class PlaylistUi(
    val songs: List<Song> = listOf(),
    val topBarTitle: String = "",
    val topBarBackgroundImageUri: String = "",
)
