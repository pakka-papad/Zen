package tech.zemn.mobile.data

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.zemn.mobile.data.music.Album
import tech.zemn.mobile.data.music.Artist
import tech.zemn.mobile.data.music.Song
import tech.zemn.mobile.data.music.SongDao

@Database(entities = [
        Song::class,
        Album::class,
        Artist::class
    ],
    version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun songDao(): SongDao

}