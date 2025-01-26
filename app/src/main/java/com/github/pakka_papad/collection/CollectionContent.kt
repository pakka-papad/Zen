package com.github.pakka_papad.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.pakka_papad.components.SongCardV1
import com.github.pakka_papad.components.more_options.SongOptions
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.home.PlayShuffleCard
import com.github.pakka_papad.home.SongInfo


fun LazyListScope.collectionContent(
    songs: List<Song>,
    onSongClicked: (index: Int) -> Unit,
    onSongFavouriteClicked: (Song) -> Unit,
    currentSong: Song?,
    onAddToQueueClicked: (Song) -> Unit,
    onPlayAllClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
    onAddToPlaylistsClicked: (Song) -> Unit,
    isPlaylistCollection: Boolean,
    onRemoveFromPlaylistClicked: (Song) -> Unit,
) {
    item {
        PlayShuffleCard(
            onPlayAllClicked = onPlayAllClicked,
            onShuffleClicked = onShuffleClicked,
        )
    }
    itemsIndexed(
        items = songs,
        key = { index, song ->
            song.location
        }
    ) { index, song ->
        var infoVisible by remember { mutableStateOf(false) }
        SongCardV1(
            song = song,
            onSongClicked = {
                onSongClicked(index)
            },
            onFavouriteClicked = onSongFavouriteClicked,
            songOptions = listOf(
                SongOptions.Info { infoVisible = true },
                SongOptions.AddToQueue { onAddToQueueClicked(song) },
                SongOptions.AddToPlaylist { onAddToPlaylistsClicked(song) },
            ) + if (isPlaylistCollection) listOf(
                SongOptions.RemoveFromPlaylist { onRemoveFromPlaylistClicked(song) }
            ) else listOf(),
            currentlyPlaying = (song.location == currentSong?.location)
        )
        if (infoVisible) {
            SongInfo(song) { infoVisible = false }
        }
    }
}

@Composable
fun CollectionImage(
    imageUri: String? = "",
    title: String? = ""
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Text(
            text = title ?: "",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                    )
                )
                .padding(10.dp),
            maxLines = 2,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.ExtraBold,
            overflow = TextOverflow.Ellipsis
        )
    }
}