package com.github.pakka_papad.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeTopBar(
    onSettingsClicked: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        SmallTopAppBar(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = "Zen Music Player",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            actions = {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "settings-btn",
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
                            onClick = onSettingsClicked
                        ),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}