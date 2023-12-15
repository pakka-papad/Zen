package com.github.pakka_papad.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    modifier: Modifier = Modifier
) {
    if (song == null) return
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = song.artUri,
            contentDescription = stringResource(R.string.song_image),
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
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            painter = painterResource(
                if (showPlayButton) R.drawable.ic_baseline_play_arrow_40 else R.drawable.ic_baseline_pause_40
            ),
            contentDescription = stringResource(
                if (showPlayButton) R.string.play_button else R.string.pause_button
            ),
            modifier = Modifier
                .size(56.dp)
                .padding(6.dp)
                .clickable(
                    onClick = onPausePlayPressed,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        radius = 22.dp
                    )
                ),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}