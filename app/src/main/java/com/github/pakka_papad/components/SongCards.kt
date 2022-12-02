package com.github.pakka_papad.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.pakka_papad.data.music.Song
import kotlinx.coroutines.launch

sealed class SongCardDropdownOptions(open val onClick: (Song) -> Unit, open val text: String) {
    data class AddToQueue(override val onClick: (Song) -> Unit) :
        SongCardDropdownOptions(onClick, "Add to queue")

    data class AddToPlaylists(override val onClick: (Song) -> Unit) :
        SongCardDropdownOptions(onClick, "Add to playlist")
}

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
    dropdownOptions: List<SongCardDropdownOptions>,
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
                    fontSize = 20.sp,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (currentlyPlaying) onCurrentlyPlayingBackgroundColor else onBackgroundColor
                )
                Text(
                    text = "${song.artist}  •  ${song.durationFormatted}",
                    maxLines = 1,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (currentlyPlaying) onCurrentlyPlayingBackgroundColor else onBackgroundColor
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
                            interactionSource = MutableInteractionSource()
                        )
                        .padding(8.dp),
                    tint = if (currentlyPlaying) onCurrentlyPlayingBackgroundColor else onBackgroundColor,
                )
                if (dropdownOptions.isNotEmpty()) {
                    var dropDownMenuExpanded by remember { mutableStateOf(false) }
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                onClick = {
                                    dropDownMenuExpanded = true
                                },
                                indication = rememberRipple(
                                    bounded = false,
                                    radius = 20.dp
                                ),
                                interactionSource = MutableInteractionSource()
                            )
                            .padding(8.dp),
                        tint = if (currentlyPlaying) onCurrentlyPlayingBackgroundColor else onBackgroundColor,
                    )
                    DropdownMenu(
                        expanded = dropDownMenuExpanded,
                        onDismissRequest = {
                            dropDownMenuExpanded = false
                        },
                        content = {
                            dropdownOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option.text,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    onClick = {
                                        option.onClick(song)
                                        dropDownMenuExpanded = false
                                    }
                                )
                            }
                        },
                        offset = DpOffset(x = 0.dp, y = (-20).dp),
                    )
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
    dropdownOptions: List<SongCardDropdownOptions>,
) = SongCardBase(
    song = song,
    onSongClicked = onSongClicked,
    onFavouriteClicked = onFavouriteClicked,
    currentlyPlaying = currentlyPlaying,
    backgroundColor = MaterialTheme.colorScheme.surface,
    currentlyPlayingBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
    onBackgroundColor = MaterialTheme.colorScheme.onSurface,
    onCurrentlyPlayingBackgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
    dropdownOptions = dropdownOptions,
)

@Composable
fun SongCardV2(
    song: Song,
    onSongClicked: () -> Unit,
    onFavouriteClicked: (Song) -> Unit,
    currentlyPlaying: Boolean = false,
    dropdownOptions: List<SongCardDropdownOptions> = listOf(),
) = SongCardBase(
    song = song,
    onSongClicked = onSongClicked,
    onFavouriteClicked = onFavouriteClicked,
    currentlyPlaying = currentlyPlaying,
    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
    currentlyPlayingBackgroundColor = MaterialTheme.colorScheme.secondary,
    onBackgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
    onCurrentlyPlayingBackgroundColor = MaterialTheme.colorScheme.onSecondary,
    dropdownOptions = dropdownOptions
)