package com.github.pakka_papad.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.R
import com.github.pakka_papad.components.PlaylistCard
import com.github.pakka_papad.components.more_options.PlaylistOptions
import com.github.pakka_papad.data.music.PlaylistWithSongCount

@Composable
fun Playlists(
    playlistsWithSongCount: List<PlaylistWithSongCount>?,
    onPlaylistClicked: (Long) -> Unit,
    listState: LazyListState,
    onPlaylistCreate: (String) -> Unit,
    onFavouritesClicked: () -> Unit,
    onDeletePlaylistClicked: (PlaylistWithSongCount) -> Unit,
) {
    if (playlistsWithSongCount == null) return
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = listState,
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
    ) {
        item {
            CreatePlaylistCard(
                onPlaylistCreate = onPlaylistCreate,
                onFavouritesClicked = onFavouritesClicked
            )
        }
        items(
            items = playlistsWithSongCount,
            key = { it.playlistId }
        ) { playlist ->
            PlaylistCard(
                playlistWithSongCount = playlist,
                onPlaylistClicked = onPlaylistClicked,
                options = listOf(
                    PlaylistOptions.DeletePlaylist {
                        onDeletePlaylistClicked(playlist)
                    }
                )
            )
        }
    }
}

@Composable
fun CreatePlaylistCard(
    onPlaylistCreate: (String) -> Unit,
    onFavouritesClicked: () -> Unit,
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    val spacerModifier = Modifier.width(8.dp)
    val iconModifier = Modifier.size(24.dp)
    val configuration = LocalConfiguration.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onFavouritesClicked,
            modifier = Modifier
                .weight(1f),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_baseline_favorite_24),
                modifier = iconModifier,
                contentDescription = "create-new-playlist"
            )
            if (configuration.screenWidthDp > 340){
                Spacer(spacerModifier)
                Text(
                    text = "Favourites",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Button(
            onClick = { isDialogVisible = true },
            modifier = Modifier
                .weight(1f),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_baseline_playlist_add_40),
                modifier = iconModifier,
                contentDescription = "create-new-playlist"
            )
            if (configuration.screenWidthDp > 340) {
                Spacer(spacerModifier)
                Text(
                    text = "New Playlist",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    AnimatedVisibility (isDialogVisible) {
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
                ) {
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