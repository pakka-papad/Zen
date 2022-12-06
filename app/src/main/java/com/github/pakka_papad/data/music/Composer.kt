package com.github.pakka_papad.data.music

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.pakka_papad.Constants

@Entity(tableName = Constants.Tables.COMPOSER_TABLE)
data class Composer(
    @PrimaryKey val name: String,
)
