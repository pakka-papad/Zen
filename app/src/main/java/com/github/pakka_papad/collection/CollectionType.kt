package com.github.pakka_papad.collection

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CollectionType(val type: Int, val id: String = ""): Parcelable {
    companion object {
        const val AlbumType = 0
        const val ArtistType = 1
        const val PlaylistType = 2
        const val AlbumArtistType = 3
        const val ComposerType = 4
        const val LyricistType = 5
        const val GenreType = 6
        const val FavouritesType = 7
    }
}