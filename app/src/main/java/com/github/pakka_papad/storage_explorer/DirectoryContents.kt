package com.github.pakka_papad.storage_explorer

import com.github.pakka_papad.data.music.Song

data class DirectoryContents(
    val directories: List<Directory> = listOf(),
    val songs: List<Song> = listOf()
)
