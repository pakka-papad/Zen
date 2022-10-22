package tech.zemn.mobile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Screens: Parcelable {
    AllSongs, Albums, Artists
}
