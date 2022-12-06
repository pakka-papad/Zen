package com.github.pakka_papad.data.music

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.pakka_papad.Constants

@Entity(tableName = Constants.Tables.ALBUM_ARTIST_TABLE)
data class AlbumArtist(
    @PrimaryKey val name: String
)
