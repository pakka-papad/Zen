package tech.zemn.mobile.data.music

import android.graphics.Bitmap

data class Album(
    val name: String,
    val albumArt: Bitmap? = null,
    val songs: List<Song> = emptyList()
)
