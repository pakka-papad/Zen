package com.github.pakka_papad.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.components.PlaylistCard
import com.github.pakka_papad.data.music.Playlist

@Composable
fun Playlists(
    playlists: List<Playlist>?,
    onPlaylistClicked: (Long) -> Unit,
    listState: LazyListState,
    onPlaylistCreate: (String) -> Unit,
) {
    if (playlists == null) return
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = listState,
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
    ) {
        item {
            CreatePlaylistCard(
                onPlaylistCreate = onPlaylistCreate,
            )
        }
        items(
            items = playlists,
            key = { it.playlistId }
        ) { playlist ->
            PlaylistCard(
                playlist = playlist,
                onPlaylistClicked = onPlaylistClicked
            )
        }
    }
}

@Composable
fun CreatePlaylistCard(
    onPlaylistCreate: (String) -> Unit
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { isDialogVisible = true },
        contentAlignment = Alignment.Center
    ) {
        var playlistName by remember { mutableStateOf("") }
        Text(
            text = "Create new playlist",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        if (isDialogVisible) {
            AlertDialog(
                onDismissRequest = { isDialogVisible = false },
                confirmButton = {
                    Button(
                        onClick = {
                            isDialogVisible = false
                            onPlaylistCreate(playlistName)
                            playlistName = ""
                        }
                    ) {
                        Text(
                            text = "Create",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            isDialogVisible = false
                            playlistName = ""
                        }
                    ){
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                },
                title = {
                    Text(
                        text = "Create playlist"
                    )
                },
                text = {
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = {
                            playlistName = it
                        },
                        label = {
                            Text(text = "Playlist name")
                        },
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }
            )
        }
    }
}