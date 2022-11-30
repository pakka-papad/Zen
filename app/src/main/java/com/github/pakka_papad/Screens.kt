package com.github.pakka_papad

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Screens: Parcelable {
    AllSongs, Albums, Artists
}
