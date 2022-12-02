package com.github.pakka_papad.playlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.home.AllSongs

@Composable
fun PlaylistContent(
    paddingValues: PaddingValues,
    songs: List<Song>,
    songsListState: LazyListState,
    onSongClicked: (index: Int) -> Unit,
    onSongFavouriteClicked: (Song) -> Unit,
    currentSong: Song?,
    onAddToQueueClicked: (Song) -> Unit,
    onPlayAllClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
    onAddToPlaylistsClicked: (Song) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        /**
         * The message passed for a playlist which has no songs
         * Albums and Artists will at least have one song
         */
        AllSongs(
            songs = songs,
            onSongClicked = onSongClicked,
            paddingValues = paddingValues,
            listState = songsListState,
            onFavouriteClicked = onSongFavouriteClicked,
            currentSong = currentSong,
            onAddToQueueClicked = onAddToQueueClicked,
            onPlayAllClicked = onPlayAllClicked,
            onShuffleClicked = onShuffleClicked,
            onAddToPlaylistsClicked = onAddToPlaylistsClicked,
            emptyListMessage = "No songs here\nTo add a song, go to song => more => add to playlist"
        )
    }
}