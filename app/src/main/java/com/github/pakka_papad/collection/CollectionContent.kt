package com.github.pakka_papad.collection

import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
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