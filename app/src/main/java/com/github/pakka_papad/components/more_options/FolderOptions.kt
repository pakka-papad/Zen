package com.github.pakka_papad.components.more_options

import androidx.annotation.DrawableRes
import com.github.pakka_papad.R

sealed class FolderOptions(
    override val onClick: () -> Unit,
    override val text: String,
    @DrawableRes override val icon: Int,
) : MoreOptions(
    onClick = onClick,
    text = text,
    icon = icon,
){
    data class Blacklist(override val onClick: () -> Unit):
            FolderOptions(
                onClick = onClick,
                text = "Blacklist Folder",
                icon = R.drawable.ic_baseline_remove_circle_40,
            )
}