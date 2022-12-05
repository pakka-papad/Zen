package com.github.pakka_papad.components.more_options

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OptionsDropDown(
    options: List<MoreOptions>,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
) {
    if (options.isEmpty()) return
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                onClick = option.onClick,
                text = {
                    Text(
                        text = option.text,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(option.icon),
                        modifier = Modifier.size(24.dp),
                        contentDescription = option.text
                    )
                }
            )
        }
    }
}


@Preview
@Composable
private fun OptionsDropDownPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        OptionsDropDown(
            onDismissRequest = { },
            options = listOf(
                SongOptions.Info { },
                SongOptions.AddToPlaylist { },
                SongOptions.AddToQueue { },
                SongOptions.RemoveFromQueue { },
                SongOptions.RemoveFromPlaylist { },
            ),
            expanded = true
        )
    }
}