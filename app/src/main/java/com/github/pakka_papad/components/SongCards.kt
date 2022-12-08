package com.github.pakka_papad.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.pakka_papad.components.more_options.OptionsAlertDialog
import com.github.pakka_papad.components.more_options.SongOptions
import com.github.pakka_papad.data.music.Song
import kotlinx.coroutines.launch

@Composable
private fun SongCardBase(
    song: Song,
    onSongClicked: () -> Unit,
    onFavouriteClicked: (Song) -> Unit,
    currentlyPlaying: Boolean,
    backgroundColor: Color,
    currentlyPlayingBackgroundColor: Color,
    onBackgroundColor: Color,
    onCurrentlyPlayingBackgroundColor: Color,
    songOptions: List<SongOptions>,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .clickable(
                onClick = onSongClicked
            )
            .background(if (currentlyPlaying) currentlyPlayingBackgroundColor else backgroundColor),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = song.artUri,
                contentDescription = "song-${song.title}-art",
                modifier = Modifier
                    .size(80.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (currentlyPlaying) onCurrentlyPlayingBackgroundColor else onBackgroundColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = song.artist,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (currentlyPlaying) onCurrentlyPlayingBackgroundColor else onBackgroundColor
                )
            }
            Row(
                modifier = Modifier
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
                        .padding(8.dp)
                        .scale(favouriteButtonScale.value)
                        .clickable(
                            onClick = {
                                onFavouriteClicked(song)
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
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    tint = if (currentlyPlaying) onCurrentlyPlayingBackgroundColor else onBackgroundColor,
                )
                if (songOptions.isNotEmpty()) {
                    var optionsVisible by remember { mutableStateOf(false) }
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp)
                            .clickable(
                                onClick = {
                                    optionsVisible = true
                                },
                                indication = rememberRipple(
                                    bounded = false,
                                    radius = 20.dp
                                ),
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        tint = if (currentlyPlaying) onCurrentlyPlayingBackgroundColor else onBackgroundColor,
                    )
                    if (optionsVisible){
                        OptionsAlertDialog(
                            options = songOptions,
                            title = song.title,
                            onDismissRequest = {
                                optionsVisible = false
                            }
                        )
                    }
                }
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

@Composable
fun SongCardV1(
    song: Song,
    onSongClicked: () -> Unit,
    onFavouriteClicked: (Song) -> Unit,
    currentlyPlaying: Boolean = false,
    songOptions: List<SongOptions>,
) = SongCardBase(
    song = song,
    onSongClicked = onSongClicked,
    onFavouriteClicked = onFavouriteClicked,
    currentlyPlaying = currentlyPlaying,
    backgroundColor = MaterialTheme.colorScheme.surface,
    currentlyPlayingBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
    onBackgroundColor = MaterialTheme.colorScheme.onSurface,
    onCurrentlyPlayingBackgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
    songOptions = songOptions,
)

@Composable
fun SongCardV2(
    song: Song,
    onSongClicked: () -> Unit,
    onFavouriteClicked: (Song) -> Unit,
    currentlyPlaying: Boolean = false,
    songOptions: List<SongOptions> = listOf(),
) = SongCardBase(
    song = song,
    onSongClicked = onSongClicked,
    onFavouriteClicked = onFavouriteClicked,
    currentlyPlaying = currentlyPlaying,
    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
    currentlyPlayingBackgroundColor = MaterialTheme.colorScheme.secondary,
    onBackgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
    onCurrentlyPlayingBackgroundColor = MaterialTheme.colorScheme.onSecondary,
    songOptions = songOptions
)