package com.github.pakka_papad.home

import android.media.MediaMetadataRetriever
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.pakka_papad.R
import com.github.pakka_papad.data.music.Song

@Composable
fun MiniPlayer(
    showPlayButton: Boolean,
    onPausePlayPressed: () -> Unit,
    song: Song?,
    paddingValues: PaddingValues,
    onMiniPlayerClicked: () -> Unit,
) {
    if (song == null) return
    var picture by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(key1 = song.location) {
        val extractor = MediaMetadataRetriever().apply {
            setDataSource(song.location)
        }
        picture = extractor.embeddedPicture
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(paddingValues)
            .clickable(
                onClick = onMiniPlayerClicked,
                indication = null,
                interactionSource = MutableInteractionSource()
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = picture,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            text = song.title,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            painter = painterResource(
                if (showPlayButton) R.drawable.ic_baseline_play_arrow_24 else R.drawable.ic_baseline_pause_24
            ),
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .padding(6.dp)
                .clickable(
                    onClick = onPausePlayPressed,
                    interactionSource = remember{ MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        radius = 22.dp
                    )
                )
        )
    }
}