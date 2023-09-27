package com.github.pakka_papad.settings.blacklisted_folder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.data.music.BlacklistedFolder

@Composable
fun BlacklistedFolders(
    folders: List<BlacklistedFolder>,
    paddingValues: PaddingValues,
    onBlacklistRemoveRequest: (BlacklistedFolder) -> Unit,
) {
    LazyColumn(
        contentPadding = paddingValues,

        ) {
        items(
            items = folders,
            key = { it.path }
        ) {
            BlacklistedFolder(
                folder = it,
                onBlacklistRemoveRequest = { onBlacklistRemoveRequest(it) }
            )
        }
    }
}

@Composable
fun BlacklistedFolder(
    folder: BlacklistedFolder,
    onBlacklistRemoveRequest: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(12.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = folder.path,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "remove from blacklist",
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    onClick = onBlacklistRemoveRequest,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        radius = 20.dp,
                        bounded = false
                    )
                ),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}