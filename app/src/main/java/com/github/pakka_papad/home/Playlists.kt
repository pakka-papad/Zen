package com.github.pakka_papad.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.R
import com.github.pakka_papad.components.PlaylistCardV2
import com.github.pakka_papad.components.more_options.PlaylistOptions
import com.github.pakka_papad.data.music.PlaylistWithSongCount
import com.github.pakka_papad.ui.theme.harmonize

@Composable
fun Playlists(
    playlistsWithSongCount: List<PlaylistWithSongCount>?,
    onPlaylistClicked: (Long) -> Unit,
    listState: LazyGridState,
    onPlaylistCreate: (String) -> Unit,
    onFavouritesClicked: () -> Unit,
    onDeletePlaylistClicked: (PlaylistWithSongCount) -> Unit,
) {
    if (playlistsWithSongCount == null) return
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize(),
        state = listState,
        columns = GridCells.Adaptive(150.dp),
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
    ) {
        item {
            CreatePlaylistCard(
                onPlaylistCreate = onPlaylistCreate,
            )
        }
        item {
            FavouritesCard(
                onFavouritesClicked = onFavouritesClicked,
            )
        }
        items(
            items = playlistsWithSongCount,
            key = { it.playlistId }
        ) { playlist ->
            PlaylistCardV2(
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
private fun FavouritesCard(
    onFavouritesClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(max = 200.dp)
            .clickable(onClick = onFavouritesClicked)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier
                .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = false)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(Brush.linearGradient(
                    colors = listOf(harmonize(Color(0xFFE90064)), harmonize(Color(0xFFFF89B8)))
                ))
                .padding(45.dp),
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = stringResource(R.string.favourite_button),
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = stringResource(R.string.favourites),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CreatePlaylistCard(
    onPlaylistCreate: (String) -> Unit,
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .widthIn(max = 200.dp)
            .clickable(onClick = { isDialogVisible = true })
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_baseline_playlist_add_40),
            modifier = Modifier
                .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = false)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(Brush.linearGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                ))
                .padding(45.dp),
            contentDescription = stringResource(R.string.create_playlist_button),
            tint = MaterialTheme.colorScheme.onPrimary,
        )
        Text(
            text = stringResource(R.string.new_playlist),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis
        )
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
                        text = stringResource(R.string.create),
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
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.create_playlist)
                )
            },
            text = {
                OutlinedTextField(
                    value = playlistName,
                    onValueChange = {
                        playlistName = it
                    },
                    label = {
                        Text(text = stringResource(R.string.playlist_name))
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    singleLine = true,
                )
            }
        )
    }
}