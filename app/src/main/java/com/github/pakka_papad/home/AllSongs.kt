package com.github.pakka_papad.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.pakka_papad.components.EmptyListMessage
import com.github.pakka_papad.components.SongCardDropdownOptions
import com.github.pakka_papad.components.SongCardV1
import com.github.pakka_papad.data.music.Song

@Composable
fun AllSongs(
    songs: List<Song>,
    onSongClicked: (index: Int) -> Unit,
    paddingValues: PaddingValues,
    listState: LazyListState,
    onFavouriteClicked: (Song) -> Unit,
    currentSong: Song?,
    onAddToQueueClicked: (Song) -> Unit,
    onPlayAllClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
    onAddToPlaylistsClicked: (Song) -> Unit,
    emptyListMessage: String = "No Songs"
) {
    if (songs.isEmpty()) {
        EmptyListMessage(
            message = emptyListMessage,
            paddingValues = paddingValues
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState,
            contentPadding = paddingValues
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
                SongCardV1(
                    song = song,
                    onSongClicked = {
                        onSongClicked(index)
                    },
                    onFavouriteClicked = onFavouriteClicked,
                    dropdownOptions = listOf(
                        SongCardDropdownOptions.AddToQueue(onAddToQueueClicked),
                        SongCardDropdownOptions.AddToPlaylists(onAddToPlaylistsClicked)
                    ),
                    currentlyPlaying = (song.location == currentSong?.location)
                )
            }
        }
    }
}