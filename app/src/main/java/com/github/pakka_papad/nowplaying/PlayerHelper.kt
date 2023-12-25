package com.github.pakka_papad.nowplaying

import android.app.PendingIntent
import androidx.compose.runtime.Stable
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer

@Stable
class PlayerHelper(
    private val exoPlayer: ExoPlayer,
    private val pausePlayIntent: PendingIntent,
    private val previousIntent: PendingIntent,
    private val nextIntent: PendingIntent,
) {
    val currentPosition: Float
        get() = exoPlayer.currentPosition.toFloat()

    val duration: Float
        get() = exoPlayer.duration.toFloat()

    val currentMediaItemIndex: Int
        get() = exoPlayer.currentMediaItemIndex

    fun addListener(listener: Listener) {
        exoPlayer.addListener(listener)
    }

    fun removeListener(listener: Listener) {
        exoPlayer.removeListener(listener)
    }

    fun seekTo(mediaItemIndex: Int, positionMs: Long) {
        exoPlayer.seekTo(mediaItemIndex, positionMs)
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    fun broadcastPausePlay() = pausePlayIntent.send()

    fun broadcastPrevious() = previousIntent.send()

    fun broadcastNext() = nextIntent.send()
}