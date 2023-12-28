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
    @PrimaryKey(autoGenerate = true) val id: Long,
    val location: String,
    val lastUpdatedOn: Long,
    val artCount: Int,
    val deleteThis: Boolean,
)

data class ThumbnailWithoutId(
    val location: String,
    val lastUpdatedOn: Long,
    val artCount: Int,
    val deleteThis: Boolean,
)
