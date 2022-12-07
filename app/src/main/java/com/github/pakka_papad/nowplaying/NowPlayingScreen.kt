package com.github.pakka_papad.nowplaying

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.media.MediaMetadataRetriever
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.github.pakka_papad.R
import com.github.pakka_papad.data.music.Song
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
                    .fillMaxHeight()
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
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AlbumArt(
    song: Song,
    modifier: Modifier = Modifier,
) {
    var picture by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(key1 = song.location) {
        val extractor = MediaMetadataRetriever().apply {
            setDataSource(song.location)
        }
        picture = extractor.embeddedPicture
        extractor.release()
    }
    AsyncImage(
        model = picture,
        contentDescription = null,
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
        MusicSlider(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 14.dp, horizontal = 24.dp),
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
        contentDescription = "like button",
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
                }, indication = rememberRipple(
                    bounded = false, radius = 25.dp
                ), interactionSource = MutableInteractionSource()
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
    contentDescription = "previous button",
    modifier = modifier
        .size(70.dp)
        .clip(RoundedCornerShape(35.dp))
        .clickable(
            onClick = onPreviousPressed,
            interactionSource = MutableInteractionSource(),
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
    contentDescription = "play/pause button",
    modifier = modifier
        .size(70.dp)
        .clip(CircleShape)
        .clickable(
            onClick = onPausePlayPressed,
            interactionSource = MutableInteractionSource(),
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
    contentDescription = "next button",
    modifier = modifier
        .size(70.dp)
        .clip(RoundedCornerShape(35.dp))
        .clickable(
            onClick = onNextPressed,
            interactionSource = MutableInteractionSource(),
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
    contentDescription = "queue button",
    modifier = modifier
        .size(50.dp)
        .clickable(
            onClick = onQueueClicked, indication = rememberRipple(
                bounded = false, radius = 25.dp
            ), interactionSource = MutableInteractionSource()
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