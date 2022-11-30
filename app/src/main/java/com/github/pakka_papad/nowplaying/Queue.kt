package com.github.pakka_papad.nowplaying

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.github.pakka_papad.data.music.Song

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
            QueueSongCard(
                song = song,
                onSongClicked = {
                    onSongClicked(index)
                },
                onFavouriteClicked = {
                    onFavouriteClicked(song)
                },
                currentlyPlaying = (song.location == currentSong?.location),
            )
        }
    }
}

@Composable
fun QueueSongCard(
    song: Song,
    onSongClicked: () -> Unit,
    onFavouriteClicked: () -> Unit,
    currentlyPlaying: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .clickable(
                onClick = onSongClicked
            )
            .then(
                if (currentlyPlaying) {
                    Modifier.background(MaterialTheme.colorScheme.secondary)
                } else Modifier
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (currentlyPlaying) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "${song.artist}  •  ${song.durationFormatted}",
                    maxLines = 1,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (currentlyPlaying) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val scope = rememberCoroutineScope()
                val favouriteButtonScale = remember { Animatable(1f) }
                Icon(
                    imageVector = if (song.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .scale(favouriteButtonScale.value)
                        .clickable(
                            onClick = {
                                onFavouriteClicked()
                                scope.launch {
                                    favouriteButtonScale.animateTo(
                                        targetValue = 1.2f,
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            easing = FastOutLinearInEasing,
                                        )
                                    )
                                    favouriteButtonScale.animateTo(
                                        targetValue = 0.8f,
                                        animationSpec = tween(
                                            durationMillis = 200,
                                            easing = LinearEasing,
                                        )
                                    )
                                    favouriteButtonScale.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(
                                            durationMillis = 100,
                                            easing = FastOutLinearInEasing,
                                        )
                                    )
                                }
                            },
                            indication = rememberRipple(
                                bounded = false,
                                radius = 20.dp
                            ),
                            interactionSource = MutableInteractionSource()
                        )
                        .padding(8.dp),
                    tint = if (currentlyPlaying) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSecondaryContainer,
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