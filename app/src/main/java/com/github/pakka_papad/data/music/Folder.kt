package com.github.pakka_papad.data.music

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.pakka_papad.Constants

@Entity(tableName = Constants.Tables.FOLDER_TABLE)
data class Folder(
    @PrimaryKey val path: String,
)
