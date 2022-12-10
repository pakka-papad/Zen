package com.github.pakka_papad.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.data.music.ArtistWithSongCount

@Composable
fun Artists(
    artistsWithSongCount: List<ArtistWithSongCount>?,
    onArtistClicked: (ArtistWithSongCount) -> Unit,
    listState: LazyListState
) {
    if (artistsWithSongCount == null) return
    if (artistsWithSongCount.isEmpty()){
        FullScreenSadMessage(
            message = "No artists found",
            paddingValues = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState,
            contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
        ) {
            items(
                items = artistsWithSongCount,
                key = { it.artistName }
            ) { artist ->
                ArtistCard(
                    artistWithSongCount = artist,
                    onArtistClicked = onArtistClicked,
                )
            }
        }
    }
}

@Composable
fun ArtistCard(
    artistWithSongCount: ArtistWithSongCount,
    onArtistClicked: (ArtistWithSongCount) -> Unit,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .clickable(
                onClick = {
                    onArtistClicked(artistWithSongCount)
                }
            ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = artistWithSongCount.artistName,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "${artistWithSongCount.count} ${if(artistWithSongCount.count == 1) "song" else "songs"}",
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            onClick = {

                            },
                            indication = rememberRipple(
                                bounded = false,
                                radius = 20.dp
                            ),
                            interactionSource = remember{ MutableInteractionSource() }
                        )
                        .padding(8.dp)
                )
            }
        }
        Spacer(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(0.8.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}