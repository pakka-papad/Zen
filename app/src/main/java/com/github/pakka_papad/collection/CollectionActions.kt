package com.github.pakka_papad.collection

import androidx.annotation.DrawableRes
import com.github.pakka_papad.R
import com.github.pakka_papad.components.more_options.MoreOptions

sealed class CollectionActions(
    override val onClick: () -> Unit,
    override val text: String,
    @DrawableRes override val icon: Int,
): MoreOptions(
    onClick = onClick,
    text = text,
    icon = icon,
) {
    data class AddToQueue(override val onClick: () -> Unit) :
        CollectionActions(
            onClick = onClick,
            text = "Add all to queue",
            icon = R.drawable.ic_baseline_queue_music_40
        )

    data class AddToPlaylist(override val onClick: () -> Unit) :
        CollectionActions(
            onClick = onClick,
            text = "Add all to playlist",
            icon = R.drawable.ic_baseline_playlist_add_40
        )
}
