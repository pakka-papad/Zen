package com.github.pakka_papad.nowplaying

import android.content.res.ColorStateList
import android.widget.SeekBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.github.pakka_papad.R
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.toMS
import kotlinx.coroutines.delay

@Composable
fun MusicSlider(
    modifier: Modifier,
    playerHelper: PlayerHelper,
    currentSongPlaying: Boolean?,
    song: Song, // to update slider when song is changed in paused state
    duration: Long,
) {
    var currentValue by remember { mutableStateOf(playerHelper.currentPosition.toLong()) }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                currentValue = 0L
            }
        }
        playerHelper.addListener(listener)
        onDispose {
            playerHelper.removeListener(listener)
        }
    }
    if (currentSongPlaying == true) {
        LaunchedEffect(Unit) {
            while (true) {
                currentValue = playerHelper.currentPosition.toLong()
                delay(33)
            }
        }
    } else {
        currentValue = playerHelper.currentPosition.toLong()
    }
    val primaryColor = MaterialTheme.colorScheme.primary
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        AndroidView(
            factory = { context ->
                SeekBar(context).apply {
                    setOnSeekBarChangeListener(
                        object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar?,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                if (fromUser) {
                                    currentValue = progress.toLong()
                                }
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                                playerHelper.seekTo(currentValue)
                            }
                        }
                    )
                    thumb = resources.getDrawable(R.drawable.seekbar_thumb, null)
                    progressDrawable = resources.getDrawable(R.drawable.progress, null)
                    thumbTintList =
                        colorStateListOf(
                            intArrayOf(android.R.attr.state_enabled) to primaryColor.toArgb(),
                        )
                    progressBackgroundTintList =
                        colorStateListOf(
                            intArrayOf(android.R.attr.state_enabled) to primaryColor.copy(alpha = 0.3f).toArgb(),
                        )
                    progressTintList =
                        colorStateListOf(
                            intArrayOf(android.R.attr.state_enabled) to primaryColor.toArgb(),
                        )
                }
            },
            update = { seekBar ->
                seekBar.max = duration.toInt()
                seekBar.progress = currentValue.toInt()
            },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = currentValue.toMS(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = duration.toMS(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

fun colorStateListOf(vararg mapping: Pair<IntArray, Int>): ColorStateList {
    val (states, colors) = mapping.unzip()
    return ColorStateList(states.toTypedArray(), colors.toIntArray())
}
