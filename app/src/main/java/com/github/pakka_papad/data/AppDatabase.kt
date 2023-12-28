package com.github.pakka_papad.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.pakka_papad.data.analytics.PlayHistory
import com.github.pakka_papad.data.analytics.PlayHistoryDao
import com.github.pakka_papad.data.daos.AlbumArtistDao
import com.github.pakka_papad.data.daos.AlbumDao
import com.github.pakka_papad.data.daos.ArtistDao
import com.github.pakka_papad.data.daos.BlacklistDao
import com.github.pakka_papad.data.daos.BlacklistedFolderDao
import com.github.pakka_papad.data.daos.ComposerDao
import com.github.pakka_papad.data.daos.GenreDao
import com.github.pakka_papad.data.daos.LyricistDao
import com.github.pakka_papad.data.daos.PlaylistDao
import com.github.pakka_papad.data.daos.SongDao
import com.github.pakka_papad.data.music.Album
import com.github.pakka_papad.data.music.AlbumArtist
import com.github.pakka_papad.data.music.Artist
import com.github.pakka_papad.data.music.BlacklistedFolder
import com.github.pakka_papad.data.music.BlacklistedSong
import com.github.pakka_papad.data.music.Composer
import com.github.pakka_papad.data.music.Genre
import com.github.pakka_papad.data.music.Lyricist
import com.github.pakka_papad.data.music.Playlist
import com.github.pakka_papad.data.music.PlaylistSongCrossRef
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.data.thumbnails.Thumbnail
import com.github.pakka_papad.data.thumbnails.ThumbnailDao

@Database(entities = [
        Song::class,
        Album::class,
        Artist::class,
        Playlist::class,
        PlaylistSongCrossRef::class,
        Genre::class,
        AlbumArtist::class,
        Composer::class,
        Lyricist::class,
        BlacklistedSong::class,
        BlacklistedFolder::class,
        PlayHistory::class,
        Thumbnail::class,
    ],
    version = 3, exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun songDao(): SongDao

    abstract fun albumDao(): AlbumDao

    abstract fun artistDao(): ArtistDao

    abstract fun albumArtistDao(): AlbumArtistDao

    abstract fun composerDao(): ComposerDao

    abstract fun lyricistDao(): LyricistDao

    abstract fun genreDao(): GenreDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun blacklistDao(): BlacklistDao

    abstract fun blacklistedFolderDao(): BlacklistedFolderDao

    abstract fun playHistoryDao(): PlayHistoryDao

    abstract fun thumbnailDao(): ThumbnailDao
}