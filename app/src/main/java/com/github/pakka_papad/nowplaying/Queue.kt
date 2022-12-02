package com.github.pakka_papad.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.home.SongCardV2

@Composable
fun Queue(
    queue: List<Song>,
    onSongClicked: (index: Int) -> Unit,
    onFavouriteClicked: (Song) -> Unit,
    currentSong: Song?,
) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 700.dp),
        contentPadding = WindowInsets.systemBars
            .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
            .asPaddingValues()
    ) {
        itemsIndexed(
            items = queue,
            key = { index, song ->
                song.location
            }
        ) { index, song ->
            SongCardV2(
                song = song,
                onSongClicked = {
                    onSongClicked(index)
                },
                onFavouriteClicked = onFavouriteClicked,
                currentlyPlaying = (song.location == currentSong?.location)
            )
        }
    }
}