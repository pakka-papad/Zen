package tech.zemn.mobile.data.music

data class Song(
    val location: String = "",
    val title: String?,
    val album: String? = "",
    val size: Float = 0f,
    val addedTimestamp: String? = "",
    val modifiedTimestamp: String? = "",
    val fileName: String = "",
    val encoding: String = "",
    val channels: String = "",
    val lastPlayedTimestamp: Long = 0,
    val played: Int = 0,
    val metadata: Metadata
){
    data class Metadata(
        val artist: String? = "",
        val albumArtist: String? = "",
        val composer: String? = "",
        val genre: String? = "",
        val lyricist: String? = "",
        val trackNumber: String? = "",
        val discNumber: String? = "",
        val year: String? = "",
        val comment: String = "",
        val duration: String? = "",
        val bitrate: String? = "",
        val sampleRate: Float = 0f,
        val bitsPerSample: Int = 0,
        val format: String? = "",
    )
}
