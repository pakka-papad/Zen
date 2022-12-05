package com.github.pakka_papad.data.music

import android.media.MediaMetadataRetriever

class MetadataExtractor {

    fun getSongMetadata(path: String): Song.Metadata {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val mData = Song.Metadata(
            artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown",
            albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) ?: "Unknown",
            composer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER) ?: "Unknown",
            genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: "Unknown",
            lyricist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER) ?: "Unknown",
            year = (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR) ?: "0").toInt(),
            duration = (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: "0").toLong(),
            bitrate = (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE) ?: "0").toFloat(),
            mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: "Unknown",
            sampleRate = 0f,
            bitsPerSample = 0
        )
        retriever.release()
        return mData
    }

    fun getSongEmbeddedPicture(path: String): ByteArray? {
        val extractor = MediaMetadataRetriever()
        extractor.setDataSource(path)
        return extractor.embeddedPicture
    }
}