package com.github.pakka_papad.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.components.SmallTopBar

@Composable
fun HomeTopBar(
    onSettingsClicked: () -> Unit,
    onSearchClicked: () -> Unit,
) = SmallTopBar(
    leadingIcon = { },
    title = "Zen Music Player",
    actions = {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "search-btn",
            modifier = Modifier
                .size(48.dp)
                .padding(9.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = false,
                        radius = 25.dp,
                    ),
                    onClick = onSearchClicked
                ),
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "settings-btn",
            modifier = Modifier
                .size(48.dp)
                .padding(9.dp)
                .rotate(90f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = false,
                        radius = 25.dp,
                    ),
                    onClick = onSettingsClicked
                ),
            tint = MaterialTheme.colorScheme.onSurface,
        )
    },
    titleMaxLines = 1,
    backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
    onBackgroundColor = MaterialTheme.colorScheme.onSurface,

)
