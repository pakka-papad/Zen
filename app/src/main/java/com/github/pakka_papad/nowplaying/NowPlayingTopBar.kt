package com.github.pakka_papad.nowplaying

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.components.TopBarWithBackArrow

@Composable
fun NowPlayingTopBar(
    onBackArrowPressed: () -> Unit,
    title: String,
) = TopBarWithBackArrow(
    onBackArrowPressed = onBackArrowPressed,
    title = title,
    actions = {
        Icon(
            imageVector = Icons.Outlined.MoreVert,
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .size(30.dp)
                .rotate(90f)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(
                        bounded = false,
                        radius = 25.dp,
                    ),
                    onClick = { }
                ),
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
)