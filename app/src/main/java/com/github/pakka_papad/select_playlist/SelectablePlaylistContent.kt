package com.github.pakka_papad.select_playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.data.music.Playlist

@Composable
fun SelectPlaylistContent(
    playlists: List<Playlist>,
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
                onSelectChanged = {
                    onSelectChanged(index)
                }
            )
        }
    }
}

@Composable
fun SelectablePlaylistCard(
    playlist: Playlist,
    isSelected: Boolean,
    onSelectChanged: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onSelectChanged)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = playlist.playlistName,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.background(Color.Transparent)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Check mark",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}