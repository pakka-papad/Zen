package tech.zemn.mobile.nowplaying

import android.widget.SeekBar
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import tech.zemn.mobile.R
import tech.zemn.mobile.toMS
import timber.log.Timber

@Composable
fun MusicSlider(
    modifier: Modifier,
    mediaPlayer: ExoPlayer,
    duration: Int,
) {
    var currentValue by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(mediaPlayer.isPlaying) }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying_: Boolean) {
                isPlaying = isPlaying_
            }
        }
        mediaPlayer.addListener(listener)
        onDispose {
            mediaPlayer.removeListener(listener)
        }
    }
    if (isPlaying) {
        LaunchedEffect(Unit) {
            while (true) {
                currentValue = mediaPlayer.currentPosition
                Timber.d("Updated")
                delay(1.seconds / 30)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
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
                                mediaPlayer.seekTo(currentValue)
                            }
                        }
                    )
                    thumb = resources.getDrawable(R.drawable.seekbar_thumb, null)
                    progressDrawable = resources.getDrawable(R.drawable.progress, null)
                    max = duration
                }
            },
            update = { seekBar ->
                seekBar.progress = currentValue.toInt()
                Timber.d("seekbar")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = currentValue.toMS())
            Text(text = duration.toLong().toMS())
        }
    }
}
