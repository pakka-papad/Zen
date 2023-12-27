package com.github.pakka_papad.nowplaying

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.pakka_papad.components.SongCardV2
import com.github.pakka_papad.data.music.Song
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.Queue(
    queue: List<Song>,
    onFavouriteClicked: (Song) -> Unit,
    currentSong: Song?,
    expanded: Boolean,
    playerHelper: PlayerHelper,
    onDrag: (fromIndex: Int, toIndex: Int) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = currentSong, key2 = expanded) {
        delay(600)
        if (!expanded) {
            listState.scrollToItem(playerHelper.currentMediaItemIndex)
            return@LaunchedEffect
        }
        if (!listState.isScrollInProgress) {
            listState.animateScrollToItem(playerHelper.currentMediaItemIndex)
        }
    }
    val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
        onDrag(fromIndex,toIndex)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .align(Alignment.CenterHorizontally)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .dragContainer(dragDropState),
        state = listState,
        contentPadding = WindowInsets.systemBars
            .only(WindowInsetsSides.Bottom)
            .asPaddingValues(),
    ) {
        itemsIndexed(
            items = queue,
            key = { _, song -> song.location }
        ) { index, song ->
            val isPlaying = currentSong?.location == song.location
            DraggableItem(dragDropState, index) {
                SongCardV2(
                    song = song,
                    onSongClicked = { if(!isPlaying){ playerHelper.seekTo(index,0) } },
                    onFavouriteClicked = onFavouriteClicked,
                    currentlyPlaying = isPlaying,
                )
            }
        }
    }
}