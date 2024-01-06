package com.github.pakka_papad.restore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.github.pakka_papad.components.SelectableCard
import com.github.pakka_papad.data.music.BlacklistedSong

@Composable
fun RestoreContent(
    songs: List<BlacklistedSong>,
    selectList: List<Boolean>,
    onSelectChanged: (index: Int, isSelected: Boolean) -> Unit,
){
    if (songs.size != selectList.size) return
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        itemsIndexed(
            items = songs,
            key = { index, song -> song.location }
        ) { index, song ->
            SelectableBlacklistedSong(
                song = song,
                isSelected = selectList[index],
                onSelectChange = {
                    onSelectChanged(index,it)
                }
            )
        }
    }
}

@Composable
fun SelectableBlacklistedSong(
    song: BlacklistedSong,
    isSelected: Boolean,
    onSelectChange: (isSelected: Boolean) -> Unit,
) = SelectableCard(
    isSelected = isSelected,
    onSelectChange = onSelectChange,
    content = {
        Column {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
)