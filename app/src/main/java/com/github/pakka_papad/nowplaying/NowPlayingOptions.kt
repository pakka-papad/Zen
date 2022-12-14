package com.github.pakka_papad.nowplaying

import androidx.annotation.DrawableRes
import com.github.pakka_papad.R
import com.github.pakka_papad.components.more_options.MoreOptions

sealed class NowPlayingOptions(
    override val onClick: () -> Unit,
    override val text: String,
    @DrawableRes override val icon: Int,
) : MoreOptions(
    onClick = onClick,
    text = text,
    icon = icon
) {
    data class SaveToPlaylist(override val onClick: () -> Unit) :
        NowPlayingOptions(
            onClick = onClick,
            text = "Save queue",
            icon = R.drawable.ic_baseline_playlist_add_40
        )
}
