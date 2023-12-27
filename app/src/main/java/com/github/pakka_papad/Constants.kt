package com.github.pakka_papad

object Constants {
    const val DATABASE_NAME = "zen_app_db"
    object Tables {
        const val SONG_TABLE = "song_table"
        const val ALBUM_TABLE = "album_table"
        const val ARTIST_TABLE = "artist_table"
        const val PLAYLIST_TABLE = "playlist_table"
        const val PLAYLIST_SONG_CROSS_REF_TABLE = "playlist_song_cross_ref_table"
        const val GENRE_TABLE = "genre_table"
        const val ALBUM_ARTIST_TABLE = "album_artist_table"
        const val COMPOSER_TABLE = "composer_table"
        const val LYRICIST_TABLE = "lyricist_table"
        const val BLACKLIST_TABLE = "blacklist_table" // for songs
        const val BLACKLISTED_FOLDER_TABLE = "blacklisted_folder_table"
        const val PLAY_HISTORY_TABLE = "play_history_table"
    }
    const val PACKAGE_NAME = BuildConfig.APPLICATION_ID
    const val MESSAGE_DURATION = 3500L
}