package com.github.pakka_papad.components.more_options

import androidx.annotation.DrawableRes

sealed class PersonOptions(
    override val onClick: () -> Unit,
    override val text: String,
    @DrawableRes override val icon: Int,
) : MoreOptions(
    onClick = onClick,
    text = text,
    icon = icon,
) {

}