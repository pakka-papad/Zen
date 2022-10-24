package tech.zemn.mobile.nowplaying

import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import tech.zemn.mobile.R
import tech.zemn.mobile.data.music.Song

@Composable
fun NowPlayingScreen(
    paddingValues: PaddingValues,
    song: Song,
    onPausePlayPressed: () -> Unit,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    showPlayButton: Boolean,
    exoPlayer: ExoPlayer,
) {
    var picture by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(key1 = song.location){
        val extractor = MediaMetadataRetriever().apply {
            setDataSource(song.location)
        }
        picture = extractor.embeddedPicture
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val (control, songInfo, displayImage, musicSlider) = createRefs()

        Row(
            modifier = Modifier
                .constrainAs(
                    ref = control,
                    constrainBlock = {
                        bottom.linkTo(parent.bottom, 30.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_baseline_skip_previous_24),
                contentDescription = null,
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
            )
            Image(
                painter = painterResource(
                    if (showPlayButton) R.drawable.ic_baseline_play_arrow_24 else R.drawable.ic_baseline_pause_24
                ),
                contentDescription = null,
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
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(35.dp)
                    )
                    .padding(10.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Image(
                painter = painterResource(R.drawable.ic_baseline_skip_next_24),
                contentDescription = null,
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
            )
        }

        MusicSlider(
            modifier = Modifier
                .constrainAs(
                    ref = musicSlider,
                    constrainBlock = {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(control.top,20.dp)
                    }
                )
                .padding(horizontal = 30.dp),
            mediaPlayer = exoPlayer,
            duration = song.durationMillis.toInt()
        )

        Column(
            modifier = Modifier
                .constrainAs(
                    ref = songInfo,
                    constrainBlock = {
                        bottom.linkTo(musicSlider.top,20.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = song.title,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = song.artist,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = song.album,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }

        Box(
            modifier = Modifier
                .constrainAs(
                    ref = displayImage,
                    constrainBlock = {
                        top.linkTo(parent.top,paddingValues.calculateTopPadding(),)
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