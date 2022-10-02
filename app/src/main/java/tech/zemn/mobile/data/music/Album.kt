package tech.zemn.mobile.data.music

import androidx.room.Entity
import androidx.room.PrimaryKey
import tech.zemn.mobile.Constants

@Entity(tableName = Constants.Tables.ALBUM_TABLE)
data class Album(
    @PrimaryKey val name: String,
    val albumArtUri: String? = null,
)
