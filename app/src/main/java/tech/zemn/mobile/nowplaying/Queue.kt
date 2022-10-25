package tech.zemn.mobile.nowplaying

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.zemn.mobile.data.music.Song
import tech.zemn.mobile.home.SongCard

@Composable
fun Queue(
    queue: List<Song>,
    onSongClicked: (index: Int) -> Unit,
    onFavouriteClicked: (Song) -> Unit,
    currentSongIndexInQueue: Int,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
            .heightIn(min = 120.dp),
    ) {
        itemsIndexed(queue) { index, song ->
            SongCard(
                song = song,
                onSongClicked = {
                    onSongClicked(index)
                },
                onFavouriteClicked = {
                    onFavouriteClicked(song)
                },
                currentlyPlaying = (index == currentSongIndexInQueue),
            )
        }
    }
}