package tech.zemn.mobile.home

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.sp
import tech.zemn.mobile.R
import tech.zemn.mobile.data.music.Song

@Composable
fun MiniPlayer(
    showPlayButton: Boolean,
    onPausePlayPressed: () -> Unit,
    song: Song,
    albumArt: Bitmap?,
    paddingValues: PaddingValues,
    onMiniPlayerClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding() + 10.dp)
            .fillMaxWidth(0.95f)
            .height(60.dp)
            .background(
                color = MaterialTheme.colors.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                onClick = onMiniPlayerClicked,
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(
                    bounded = true,
                    radius = 100.dp
                )
            ),
    ) {
        if (albumArt != null) {
            Image(
                bitmap = albumArt.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Text(
            modifier = Modifier
                .padding(horizontal = 64.dp, vertical = 6.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            text = song.title,
            fontSize = 18.sp,
            color = Color.White,
        )
        Image(
            painter = painterResource(
                if (showPlayButton) R.drawable.ic_baseline_play_arrow_24 else R.drawable.ic_baseline_pause_24
            ),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clickable(
                    onClick = onPausePlayPressed,
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(
                        bounded = true,
                        radius = 28.dp
                    )
                )
                .padding(8.dp)
                .align(BiasAlignment(1f, 0f)),
            colorFilter = ColorFilter.tint(Color.White),
        )
    }
}

@Preview
@Composable
fun MiniPlayerPreview() {
    MiniPlayer(
        showPlayButton = true,
        onPausePlayPressed = {  },
        song = Song(
            location = "",
            title = "Shape of You",
            album = "",
            size = 0f,
            addedTimestamp = 0,
            modifiedTimestamp = 0,
            artist = "Ed Sheeran",
            albumArtist = "",
            composer = "",
            genre = "",
            lyricist = "",
            year = 0,
            comment = null,
            duration = 18000,
            bitrate = 0f,
            sampleRate = 0f,
            bitsPerSample = 0,
            mimeType = "audio/mpeg",
        ),
        albumArt = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888),
        paddingValues = PaddingValues(all = 0.dp),
        onMiniPlayerClicked = {}
    )
}