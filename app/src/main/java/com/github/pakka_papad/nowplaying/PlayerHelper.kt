package com.github.pakka_papad.nowplaying

import androidx.compose.runtime.Stable
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer

@Stable
class PlayerHelper(
    private val exoPlayer: ExoPlayer,
) {
    val currentPosition: Float
        get() = exoPlayer.currentPosition.toFloat()

    val duration: Float
        get() = exoPlayer.duration.toFloat()

    val currentMediaItemIndex: Int
        get() = exoPlayer.currentMediaItemIndex

    fun addListener(listener: Listener) = exoPlayer::addListener

    fun removeListener(listener: Listener) = exoPlayer::removeListener

    fun seekTo(mediaItemIndex: Int, positionMs: Long) {
        exoPlayer.seekTo(mediaItemIndex, positionMs)
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }
}