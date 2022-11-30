package com.github.pakka_papad.nowplaying

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.github.pakka_papad.R
import com.github.pakka_papad.data.music.Song

@Composable
fun NowPlayingScreen(
    paddingValues: PaddingValues,
    song: Song,
    onPausePlayPressed: () -> Unit,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    showPlayButton: Boolean,
    exoPlayer: ExoPlayer,
    onFavouriteClicked: () -> Unit,
    onQueueClicked: () -> Unit,
) {
    var picture by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(key1 = song.location) {
        val extractor = MediaMetadataRetriever().apply {
            setDataSource(song.location)
        }
        picture = extractor.embeddedPicture
    }
    ConstraintLayout(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .fillMaxSize(),
    ) {
        val (control, songInfo, displayImage, musicSlider) = createRefs()

        Row(
            modifier = Modifier
                .constrainAs(
                    ref = control,
                    constrainBlock = {
                        bottom.linkTo(parent.bottom, 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val scope = rememberCoroutineScope()
            val favouriteButtonScale = remember { Animatable(1f) }
            Image(
                imageVector = if (song.favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "favourite button",
                modifier = Modifier
                    .size(50.dp)
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
                            radius = 25.dp
                        ),
                        interactionSource = MutableInteractionSource()
                    )
                    .padding(10.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
            Image(
                painter = painterResource(R.drawable.ic_baseline_skip_previous_24),
                contentDescription = "previous button",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(35.dp))
                    .clickable(
                        onClick = onPreviousPressed,
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(
                            bounded = true,
                            radius = 35.dp
                        )
                    )
                    .padding(10.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
            Image(
                painter = painterResource(
                    if (showPlayButton) R.drawable.ic_baseline_play_arrow_24 else R.drawable.ic_baseline_pause_24
                ),
                contentDescription = "play/pause button",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(35.dp))
                    .clickable(
                        onClick = onPausePlayPressed,
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(
                            bounded = true,
                            radius = 35.dp
                        )
                    )
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(35.dp)
                    )
                    .padding(10.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )
            Image(
                painter = painterResource(R.drawable.ic_baseline_skip_next_24),
                contentDescription = "next button",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(35.dp))
                    .clickable(
                        onClick = onNextPressed,
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(
                            bounded = true,
                            radius = 35.dp
                        )
                    )
                    .padding(10.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
            Image(
                painter = painterResource(R.drawable.ic_baseline_queue_music_24),
                contentDescription = "queue button",
                modifier = Modifier
                    .size(50.dp)
                    .clickable(
                        onClick = onQueueClicked,
                        indication = rememberRipple(
                            bounded = false,
                            radius = 25.dp
                        ),
                        interactionSource = MutableInteractionSource()
                    )
                    .padding(10.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
        }

        MusicSlider(
            modifier = Modifier
                .constrainAs(
                    ref = musicSlider,
                    constrainBlock = {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(control.top, 20.dp)
                    }
                )
                .padding(horizontal = 30.dp),
            mediaPlayer = exoPlayer,
            duration = song.durationMillis
        )

        Column(
            modifier = Modifier
                .constrainAs(
                    ref = songInfo,
                    constrainBlock = {
                        bottom.linkTo(musicSlider.top, 20.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = song.title,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = song.artist,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = song.album,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Box(
            modifier = Modifier
                .constrainAs(
                    ref = displayImage,
                    constrainBlock = {
                        top.linkTo(parent.top, paddingValues.calculateTopPadding())
                        bottom.linkTo(songInfo.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = picture,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.8f)
                    .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = false)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false,
                    )
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
            )
        }
    }
}