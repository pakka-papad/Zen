package com.github.pakka_papad.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.data.music.AlbumWithSongs
import timber.log.Timber

@Composable
fun Albums(
    albumsWithSongs: List<AlbumWithSongs>?,
    gridState: LazyGridState,
    onAlbumClicked: (AlbumWithSongs) -> Unit
) {
    if (albumsWithSongs == null) return
    if (albumsWithSongs.isEmpty()) {
        FullScreenSadMessage(
            message = "Oops! No albums found",
            paddingValues = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
        )
    } else {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            state = gridState,
            columns = GridCells.Adaptive(150.dp),
            contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
        ) {
            items(
                items = albumsWithSongs,
                key = { it.album.name }
            ) { album ->
                AlbumCard(
                    albumWithSongs = album,
                    onAlbumClicked = onAlbumClicked
                )
            }
        }
    }
}

@Composable
fun AlbumCard(
    albumWithSongs: AlbumWithSongs,
    onAlbumClicked: (AlbumWithSongs) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                onClick = {
                    onAlbumClicked(albumWithSongs)
                },
            ),
    ) {
        AsyncImage(
            model = albumWithSongs.album.albumArtUri,
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = false)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
            onError = {
                Timber.e(albumWithSongs.album.albumArtUri)
                Timber.e(it.result.throwable.message)
                Timber.e("AsyncImageError")
            },
            onSuccess = {
                Timber.i("AsyncImageSuccess")
            }
        )
        Text(
            text = albumWithSongs.album.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(vertical = 10.dp, horizontal = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}