package com.github.pakka_papad.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.pakka_papad.data.music.*

@Composable
fun SongCardV3(
    song: Song,
    onClick: () -> Unit,
) = Column(
    modifier = Modifier
        .widthIn(max = 200.dp)
        .fillMaxWidth()
        .padding(10.dp)
        .clickable(onClick = onClick),
    horizontalAlignment = Alignment.Start,
) {
    AsyncImage(
        model = song.artUri,
        contentDescription = "song-album-art",
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop,
    )
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
    )
    Text(
        text = song.title,
        maxLines = 1,
        style = MaterialTheme.typography.titleMedium,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = song.artist,
        maxLines = 1,
        style = MaterialTheme.typography.titleSmall,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun TextCard(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(12.dp),
        style = MaterialTheme.typography.titleLarge,
        overflow = TextOverflow.Ellipsis
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTypeSelector(
    currentType: SearchType,
    onSearchTypeSelect: (SearchType) -> Unit,
){
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        items(
            items = SearchType.values(),
            key = { it.name }
        ){ type ->
            FilterChip(
                selected = (type == currentType),
                onClick = { onSearchTypeSelect(type) },
                label = {
                    Text(
                        text = type.text,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "check-mark",
                    )
                }
            )
        }
    }
}