package com.github.pakka_papad.data.music

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.pakka_papad.Constants

@Entity(tableName = Constants.Tables.ARTIST_TABLE)
data class Artist(
    @PrimaryKey val name: String,
)
