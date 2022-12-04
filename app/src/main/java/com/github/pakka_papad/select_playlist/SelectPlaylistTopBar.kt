package com.github.pakka_papad.select_playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.components.CenterAlignedTopBar

@Composable
fun SelectPlaylistTopBar(
    onCancelClicked: () -> Unit,
    onConfirmClicked: () -> Unit,
) = CenterAlignedTopBar(
    leadingIcon = {
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .size(30.dp)
                .clickable(
                    interactionSource = remember{ MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = false,
                        radius = 25.dp,
                    ),
                    onClick = onCancelClicked
                ),
            tint = MaterialTheme.colorScheme.onSurface,
        )
    },
    title = "Select Playlists",
    actions = {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .size(30.dp)
                .clickable(
                    interactionSource = remember{ MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = false,
                        radius = 25.dp,
                    ),
                    onClick = onConfirmClicked
                ),
            tint = MaterialTheme.colorScheme.onSurface
        )
    },
    titleMaxLines = 1
)