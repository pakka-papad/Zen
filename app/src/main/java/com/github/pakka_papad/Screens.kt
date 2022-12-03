package com.github.pakka_papad

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Screens(@DrawableRes val icon: Int): Parcelable {
    Songs(R.drawable.ic_baseline_list_24),
    Albums(R.drawable.ic_baseline_album_24),
    Artists(R.drawable.ic_baseline_person_24),
    Playlists(R.drawable.ic_baseline_playlist_play_24)
}
