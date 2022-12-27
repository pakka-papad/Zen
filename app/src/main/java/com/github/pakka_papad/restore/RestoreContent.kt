package com.github.pakka_papad.restore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.data.music.BlacklistedSong

@Composable
fun RestoreContent(
    songs: List<BlacklistedSong>,
    selectList: List<Boolean>,
    paddingValues: PaddingValues,
    onSelectChanged: (index: Int, isSelected: Boolean) -> Unit,
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = paddingValues,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableBlacklistedSong(
    song: BlacklistedSong,
    isSelected: Boolean,
    onSelectChange: (isSelected: Boolean) -> Unit,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectChange
        )
        Spacer(Modifier.width(10.dp))
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
}