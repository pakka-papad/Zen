package com.github.pakka_papad.collection

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class CollectionType : Parcelable {
    data class AlbumType(val albumName: String): CollectionType()
    data class ArtistType(val artistName: String): CollectionType()
    data class PlaylistType(val id: Long): CollectionType()
}