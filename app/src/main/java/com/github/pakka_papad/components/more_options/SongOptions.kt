package com.github.pakka_papad.components.more_options

import androidx.annotation.DrawableRes
import com.github.pakka_papad.R

sealed class SongOptions(
    override val onClick: () -> Unit,
    override val text: String,
    @DrawableRes override val icon: Int,
) : MoreOptions(
    onClick = onClick,
    text = text,
    icon = icon,
) {
    data class AddToQueue(override val onClick: () -> Unit) :
        SongOptions(
            onClick = onClick,
            text = "Add to queue",
            icon = R.drawable.ic_baseline_queue_music_40
        )

    data class AddToPlaylist(override val onClick: () -> Unit) :
        SongOptions(
            onClick = onClick,
            text = "Add to playlist",
            icon = R.drawable.ic_baseline_playlist_add_40
        )

    data class Info(override val onClick: () -> Unit) :
        SongOptions(
            onClick = onClick,
            text = "Info",
            icon = R.drawable.ic_baseline_info_40
        )

    data class RemoveFromQueue(override val onClick: () -> Unit) :
        SongOptions(
            onClick = onClick,
            text = "Remove from queue",
            icon = R.drawable.ic_baseline_remove_circle_40
        )

    data class RemoveFromPlaylist(override val onClick: () -> Unit) :
        SongOptions(
            onClick = onClick,
            text = "Remove from playlist",
            icon = R.drawable.ic_baseline_playlist_remove_40,
        )
}
