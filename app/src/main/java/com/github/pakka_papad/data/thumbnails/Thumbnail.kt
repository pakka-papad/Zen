package com.github.pakka_papad.data.thumbnails

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.pakka_papad.Constants

@Entity(
    tableName = Constants.Tables.THUMBNAIL_TABLE,
    indices = [
        Index(value = ["location"], unique = true)
    ]
)
data class Thumbnail(
    @PrimaryKey val location: String,
    val addedOn: Long,
    val artCount: Int,
    val deleteThis: Boolean,
)
