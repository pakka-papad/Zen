package com.github.pakka_papad.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.pakka_papad.data.music.Album
import com.github.pakka_papad.data.music.Artist
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.data.music.SongDao

@Database(entities = [
        Song::class,
        Album::class,
        Artist::class
    ],
    version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun songDao(): SongDao

}