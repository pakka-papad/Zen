package com.github.pakka_papad.nowplaying

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.github.pakka_papad.R
import com.github.pakka_papad.data.UserPreferences.PlaybackParams
import com.github.pakka_papad.nowplaying.RepeatMode as RepeatModeEnum
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.round
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
fun NowPlayingScreen(
    paddingValues: PaddingValues,
    song: Song?,
    onPausePlayPressed: () -> Unit,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    songPlaying: Boolean?,
    exoPlayer: ExoPlayer,
    onFavouriteClicked: () -> Unit,
    onQueueClicked: () -> Unit,
    repeatMode: RepeatModeEnum,
    toggleRepeatMode: () -> Unit,
    playbackParams: PlaybackParams,
    updatePlaybackParams: (speed: Int, pitch: Int) -> Unit,
) {
    if (song == null || songPlaying == null) return
    val configuration = LocalConfiguration.current
    val screenHeight = max(configuration.screenHeightDp - 60, 0) // subtracting 60 for TopBarHeight
    val screenWidth = configuration.screenWidthDp
    if (configuration.orientation == ORIENTATION_LANDSCAPE) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                .padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            val albumArtMaxWidth = ((0.4f) * screenWidth).toInt()
            val infoAndControlsMaxWidth = ((0.6f) * screenWidth).toInt()
            if (albumArtMaxWidth >= 50 && screenHeight >= 50) {
                val imageSize = min(albumArtMaxWidth, screenHeight)
                AlbumArt(
                    song = song,
                    modifier = Modifier.size((imageSize * 0.8f).dp),
                )
            }
            InfoAndControls(
                song = song,
                onPausePlayPressed = onPausePlayPressed,
                onPreviousPressed = onPreviousPressed,
                onNextPressed = onNextPressed,
                showPlayButton = !songPlaying,
                exoPlayer = exoPlayer,
                onFavouriteClicked = onFavouriteClicked,
                onQueueClicked = onQueueClicked,
                modifier = Modifier
                    .width(infoAndControlsMaxWidth.dp)
                    .fillMaxHeight(),
                repeatMode = repeatMode,
                toggleRepeatMode = toggleRepeatMode,
                playbackParams = playbackParams,
                updatePlaybackParams = updatePlaybackParams,
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val albumArtMaxHeight = ((0.6f) * screenHeight).toInt()
            val infoAndControlsMaxHeight = ((0.4f) * screenHeight).toInt()
            if (screenWidth >= 50 && albumArtMaxHeight >= 50) {
                val imageSize = min(screenWidth, albumArtMaxHeight)
                AlbumArt(
                    song = song,
                    modifier = Modifier
                        .size((imageSize * 0.8f).dp)
                        .weight(1f),
                )
            }
            InfoAndControls(
                song = song,
                onPausePlayPressed = onPausePlayPressed,
                onPreviousPressed = onPreviousPressed,
                onNextPressed = onNextPressed,
                showPlayButton = !songPlaying,
                exoPlayer = exoPlayer,
                onFavouriteClicked = onFavouriteClicked,
                onQueueClicked = onQueueClicked,
                modifier = Modifier
                    .height(infoAndControlsMaxHeight.dp)
                    .fillMaxWidth(),
                repeatMode = repeatMode,
                toggleRepeatMode = toggleRepeatMode,
                playbackParams = playbackParams,
                updatePlaybackParams = updatePlaybackParams,
            )
        }
    }
}

@Composable
private fun AlbumArt(
    song: Song,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = song.artUri,
        contentDescription = stringResource(R.string.song_image),
        modifier = modifier
            .aspectRatio(1f)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(20.dp)),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun InfoAndControls(
    song: Song,
    onPausePlayPressed: () -> Unit,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    showPlayButton: Boolean,
    exoPlayer: ExoPlayer,
    onFavouriteClicked: () -> Unit,
    onQueueClicked: () -> Unit,
    modifier: Modifier = Modifier,
    repeatMode: RepeatModeEnum,
    toggleRepeatMode: () -> Unit,
    playbackParams: PlaybackParams,
    updatePlaybackParams: (speed: Int, pitch: Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .padding(vertical = 16.dp)
    ) {
        SongInfo(
            song = song,
            modifier = Modifier.weight(1f)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            PlaybackSpeedAndPitchController(
                playbackParams = playbackParams,
                updatePlaybackParams = updatePlaybackParams
            )
            RepeatModeController(
                currentRepeatMode = repeatMode,
                toggleRepeatMode = toggleRepeatMode
            )
        }
        MusicSlider(
            modifier = Modifier
                .weight(0.7f)
                .padding(vertical = 0.dp, horizontal = 24.dp),
            mediaPlayer = exoPlayer,
            duration = song.durationMillis,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.widthIn(max = 370.dp)
        ) {
            LikeButton(
                song = song,
                onFavouriteClicked = onFavouriteClicked,
                modifier = Modifier.weight(1f),
            )
            PreviousButton(
                onPreviousPressed = onPreviousPressed,
            )
            PausePlayButton(
                showPlayButton = showPlayButton,
                onPausePlayPressed = onPausePlayPressed,
            )
            NextButton(
                onNextPressed = onNextPressed,
            )
            QueueButton(
                onQueueClicked = onQueueClicked,
                modifier = Modifier.weight(1f),
            )
        }
    }
}


/**
 * All control buttons composable
 */
@Composable
private fun LikeButton(
    song: Song,
    onFavouriteClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val favouriteButtonScale = remember { Animatable(1f) }
    Image(
        imageVector = if (song.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
        contentDescription = stringResource(R.string.favourite_button),
        modifier = modifier
            .size(50.dp)
            .scale(favouriteButtonScale.value)
            .clickable(
                onClick = {
                    onFavouriteClicked()
                    scope.launch {
                        favouriteButtonScale.animateTo(
                            targetValue = 1.2f, animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutLinearInEasing,
                            )
                        )
                        favouriteButtonScale.animateTo(
                            targetValue = 0.8f, animationSpec = tween(
                                durationMillis = 200,
                                easing = LinearEasing,
                            )
                        )
                        favouriteButtonScale.animateTo(
                            targetValue = 1f, animationSpec = tween(
                                durationMillis = 100,
                                easing = FastOutLinearInEasing,
                            )
                        )
                    }
                },
                indication = rememberRipple(
                    bounded = false, radius = 25.dp
                ),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(10.dp),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
    )
}

@Composable
private fun PreviousButton(
    onPreviousPressed: () -> Unit,
    modifier: Modifier = Modifier,
) = Image(
    painter = painterResource(R.drawable.ic_baseline_skip_previous_40),
    contentDescription = stringResource(R.string.previous_button),
    modifier = modifier
        .size(70.dp)
        .clip(RoundedCornerShape(35.dp))
        .clickable(
            onClick = onPreviousPressed,
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(
                bounded = true, radius = 35.dp
            )
        )
        .padding(10.dp),
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
)

@Composable
private fun PausePlayButton(
    showPlayButton: Boolean,
    onPausePlayPressed: () -> Unit,
    modifier: Modifier = Modifier,
) = Image(
    painter = painterResource(
        if (showPlayButton) R.drawable.ic_baseline_play_arrow_40 else R.drawable.ic_baseline_pause_40
    ),
    contentDescription = stringResource(
        if (showPlayButton) R.string.play_button else R.string.pause_button
    ),
    modifier = modifier
        .size(70.dp)
        .clip(CircleShape)
        .clickable(
            onClick = onPausePlayPressed,
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(
                bounded = true, radius = 35.dp
            )
        )
        .background(
            color = MaterialTheme.colorScheme.primary, shape = CircleShape
        )
        .padding(10.dp),
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
)

@Composable
private fun NextButton(
    onNextPressed: () -> Unit,
    modifier: Modifier = Modifier,
) = Image(
    painter = painterResource(R.drawable.ic_baseline_skip_next_40),
    contentDescription = stringResource(R.string.next_button),
    modifier = modifier
        .size(70.dp)
        .clip(RoundedCornerShape(35.dp))
        .clickable(
            onClick = onNextPressed,
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(
                bounded = true, radius = 35.dp
            )
        )
        .padding(10.dp),
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
)

@Composable
private fun QueueButton(
    onQueueClicked: () -> Unit,
    modifier: Modifier = Modifier,
) = Image(
    painter = painterResource(R.drawable.ic_baseline_queue_music_40),
    contentDescription = stringResource(R.string.queue_button),
    modifier = modifier
        .size(50.dp)
        .clickable(
            onClick = onQueueClicked,
            indication = rememberRipple(
                bounded = false, radius = 25.dp
            ),
            interactionSource = remember { MutableInteractionSource() }
        )
        .padding(10.dp),
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
)

/**
 * Song info composable
 */
@Composable
private fun SongInfo(
    song: Song,
    modifier: Modifier = Modifier,
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier,
    verticalArrangement = Arrangement.Center
) {
    Text(
        text = song.title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
        text = song.artist,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
        text = song.album,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
fun PlaybackSpeedAndPitchController(
    playbackParams: PlaybackParams,
    updatePlaybackParams: (speed: Int, pitch: Int) -> Unit,
){
    val speed = playbackParams.playbackSpeed
    val pitch = playbackParams.playbackPitch
    var showDialog by remember { mutableStateOf(false) }
    Icon(
        painter = painterResource(R.drawable.baseline_speed_24),
        contentDescription = stringResource(R.string.speed_and_pitch_controller),
        modifier = Modifier
            .size(30.dp)
            .clickable { showDialog = true },
        tint = MaterialTheme.colorScheme.onSurface
    )
    if (showDialog) {
        var newSpeed by remember { mutableStateOf((speed.toFloat()/100).round(2))}
        var newPitch by remember { mutableStateOf((pitch.toFloat()/100).round(2)) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        updatePlaybackParams(
                            newSpeed.times(100).toInt(),
                            newPitch.times(100).toInt()
                        )
                    },
                    content = {
                        Text(text = stringResource(R.string.save))
                    }
                )
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDialog = false },
                    content = {
                        Text(text = stringResource(R.string.cancel))
                    }
                )
            },
            text = {
                Column() {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(
                            text = stringResource(R.string.speed_x, newSpeed),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.alignByBaseline()
                        )
                        TextButton(
                            onClick = { newSpeed = 1f },
                            content = {
                                Text(text = stringResource(R.string.reset))
                            },
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                    Slider(
                        value = newSpeed,
                        onValueChange = { newSpeed = it.round(2) },
                        valueRange = 0.01f..2.0f,
                        steps = 20,
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ){
                        Text(
                            text = stringResource(R.string.pitch_x, newPitch),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.alignByBaseline()
                        )
                        TextButton(
                            onClick = { newPitch = 1f },
                            content = {
                                Text(text = stringResource(R.string.reset))
                            },
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                    Slider(
                        value = newPitch,
                        onValueChange = { newPitch = it.round(2) },
                        valueRange = 0.01f..2.0f,
                        steps = 20,
                    )
                }
            }
        )
    }
}

@Composable
fun RepeatModeController(
    currentRepeatMode: RepeatModeEnum,
    toggleRepeatMode: () -> Unit,
) {
    Icon(
        painter = painterResource(currentRepeatMode.iconResource),
        contentDescription = stringResource(R.string.repeat_mode_button),
        modifier = Modifier
            .size(30.dp)
            .clickable(
                onClick = toggleRepeatMode
            ),
        tint = MaterialTheme.colorScheme.onSurface
    )
}