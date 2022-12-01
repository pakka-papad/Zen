package com.github.pakka_papad.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.pakka_papad.data.music.*

@Database(entities = [
        Song::class,
        Album::class,
        Artist::class,
        Playlist::class,
        PlaylistSongCrossRef::class,
    ],
    version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun songDao(): SongDao

}