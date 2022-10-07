package tech.zemn.mobile.data.music

import androidx.room.Entity
import androidx.room.PrimaryKey
import tech.zemn.mobile.Constants

@Entity(tableName = Constants.Tables.ARTIST_TABLE)
data class Artist(
    @PrimaryKey val name: String,
)
