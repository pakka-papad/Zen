package tech.zemn.mobile.data.music

import android.media.MediaMetadataRetriever

class MetadataExtractor {

    fun getSongMetadata(path: String): Song.Metadata {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val mData = Song.Metadata(
            artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
            albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST),
            composer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER),
            genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE),
            lyricist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER), // Doesn't work :(
            trackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS),
            discNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER),
            year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR),
            duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION),
            bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE),
            format = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
        )
        retriever.release()
        return mData
    }
}