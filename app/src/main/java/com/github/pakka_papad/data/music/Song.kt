package com.github.pakka_papad.data.music

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.pakka_papad.Constants

@Entity(tableName = Constants.Tables.SONG_TABLE)
data class Song(
    @PrimaryKey val location: String = "",
    val title: String,
    val album: String = "",
    val size: String,
    val addedDate: String,
    val modifiedDate: String,
    val artist: String,
    val albumArtist: String,
    val composer: String,
    val genre: String,
    val lyricist: String,
    val year: Int,
    val comment: String? = null,
    val durationMillis: Long,
    val durationFormatted: String,
    val bitrate: Float,
    val sampleRate: Float,
    val bitsPerSample: Int = 0,
    val mimeType: String? = null,
    val favourite: Boolean = false,
){
    data class Metadata(
        val artist: String,
        val albumArtist: String,
        val composer: String,
        val genre: String,
        val lyricist: String,
        val year: Int,
        val comment: String? = null,
        val duration: Long,
        val bitrate: Float,
        val sampleRate: Float,
        val bitsPerSample: Int = 0,
        val mimeType: String? = null,
    )
}
