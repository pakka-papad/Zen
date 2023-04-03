package com.github.pakka_papad.storage_explorer

sealed class StorageFile(
    val isDirectory: Boolean,
    open val name: String,
    open val absolutePath: String,
) {
    data class Folder(override val name: String, override val absolutePath: String):
        StorageFile(true, name, absolutePath)
    data class MusicFile(override val name: String, override val absolutePath: String):
        StorageFile(false, name, absolutePath)
}