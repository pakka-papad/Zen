package com.github.pakka_papad.components.more_options

import androidx.annotation.DrawableRes
import com.github.pakka_papad.R

sealed class PlaylistOptions(
    override val onClick: () -> Unit,
    override val text: String,
    @DrawableRes override val icon: Int,
) : MoreOptions(
    onClick = onClick,
    text = text,
    icon = icon,
) {
    data class DeletePlaylist(override val onClick: () -> Unit) :
        PlaylistOptions(
            onClick = onClick,
            text = "Delete playlist",
            icon = R.drawable.ic_baseline_playlist_remove_40
        )
}