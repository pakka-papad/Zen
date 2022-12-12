package com.github.pakka_papad.select_playlist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.pakka_papad.components.SelectablePlaylistCard
import com.github.pakka_papad.data.music.PlaylistWithSongCount

@Composable
fun SelectPlaylistContent(
    playlists: List<PlaylistWithSongCount>,
    selectList: List<Boolean>,
    paddingValues: PaddingValues,
    onSelectChanged: (index: Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = paddingValues,
    ) {
        itemsIndexed(
            items = playlists,
            key = { index, playlist -> playlist.playlistId }
        ) { index, playlist ->
            SelectablePlaylistCard(
                playlist = playlist,
                isSelected = selectList[index],
                onSelectChange = {
                    onSelectChanged(index)
                }
            )
        }
    }
}