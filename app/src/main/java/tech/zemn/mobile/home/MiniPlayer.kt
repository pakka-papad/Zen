package tech.zemn.mobile.home

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import tech.zemn.mobile.R
import tech.zemn.mobile.data.music.Song

@Composable
fun MiniPlayer(
    showPlayButton: Boolean,
    onPausePlayPressed: () -> Unit,
    song: Song,
    paddingValues: PaddingValues,
    onMiniPlayerClicked: () -> Unit
) {
    var picture by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(key1 = song.location){
        val extractor = MediaMetadataRetriever().apply {
            setDataSource(song.location)
        }
        picture = extractor.embeddedPicture
    }
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
            ),
    ) {
        AsyncImage(
            model = picture,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
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