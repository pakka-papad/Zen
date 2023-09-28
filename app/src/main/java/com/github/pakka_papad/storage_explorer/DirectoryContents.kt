package com.github.pakka_papad.storage_explorer

import com.github.pakka_papad.data.music.MiniSong

data class DirectoryContents(
    val directories: List<Directory> = listOf(),
    val songs: List<MiniSong> = listOf()
)
