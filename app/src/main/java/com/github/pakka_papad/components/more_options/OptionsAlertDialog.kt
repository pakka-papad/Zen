package com.github.pakka_papad.components.more_options

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OptionsAlertDialog(
    options: List<MoreOptions>,
    title: String? = null,
    onDismissRequest: () -> Unit,
) {
    if (options.isEmpty()) return
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            title?.let {
                Text(
                    text = it,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = "Close",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                options.forEach { option ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                onClick = {
                                    onDismissRequest()
                                    option.onClick()
                                },
                                indication = rememberRipple(radius = 160.dp),
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(option.icon),
                            contentDescription = option.text,
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            text = option.text,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun OptionsAlertDialogPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        OptionsAlertDialog(
            onDismissRequest = { },
            options = listOf(
                SongOptions.Info { },
                SongOptions.AddToPlaylist { },
                SongOptions.AddToQueue { },
                SongOptions.RemoveFromQueue { },
                SongOptions.RemoveFromPlaylist { },
            ),
            title = "Musafir Hoon Yaron"
        )
    }
}