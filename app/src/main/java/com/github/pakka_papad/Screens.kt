package com.github.pakka_papad

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

/**
 * !! Dot not change order of already added objects
 */
@Parcelize
enum class Screens(@DrawableRes val outlinedIcon: Int, @DrawableRes val filledIcon: Int): Parcelable {
    Songs(R.drawable.ic_outline_music_note_40,R.drawable.ic_baseline_music_note_40),
    Albums(R.drawable.ic_outline_album_40,R.drawable.ic_baseline_album_40),
    Artists(R.drawable.ic_outline_person_40,R.drawable.ic_baseline_person_40),
    Playlists(R.drawable.ic_outline_library_music_40,R.drawable.ic_baseline_library_music_40),
    Genres(R.drawable.ic_baseline_piano_40,R.drawable.ic_baseline_piano_40),
    Folders(R.drawable.ic_outline_folder_40,R.drawable.ic_baseline_folder_40)
}
