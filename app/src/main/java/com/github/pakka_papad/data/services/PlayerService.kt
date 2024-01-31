package com.github.pakka_papad.data.services

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.work.await
import com.github.pakka_papad.data.ZenCrashReporter
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.player.ZenPlayer
import com.github.pakka_papad.player.toMediaItem
import com.github.pakka_papad.toCorrectedParams
import com.github.pakka_papad.toExoPlayerPlaybackParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong

interface PlayerService {
    suspend fun startServiceIfNotRunning(songs: List<Song>, startPlayingFromPosition: Int)
}

class PlayerServiceImpl(
    private val context: Context,
    private val queueService: QueueService,
    private val preferenceProvider: ZenPreferenceProvider,
    private val crashReporter: ZenCrashReporter,
): PlayerService {

    private val lastCallTime = AtomicLong(0)

    @SuppressLint("RestrictedApi")
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override suspend fun startServiceIfNotRunning(songs: List<Song>, startPlayingFromPosition: Int) {
        synchronized(lastCallTime) {
            if (lastCallTime.get() + 1000 >= System.currentTimeMillis()) return
            lastCallTime.set(System.currentTimeMillis())
        }
        crashReporter.logData("PlayerService.startServiceIfNotRunning() " +
                "lastCallTime:${lastCallTime.get()} ZenPlayer.isRunning:${ZenPlayer.isRunning.get()}")

        queueService.setQueue(songs, startPlayingFromPosition)
        if (ZenPlayer.isRunning.get()) return
        MediaController.Builder(
            context,
            SessionToken(context, ComponentName(context, ZenPlayer::class.java))
        ).buildAsync().await().apply {
            withContext(Dispatchers.Main) {
                stop()
                clearMediaItems()
                addMediaItems(songs.map(Song::toMediaItem))
                prepare()
                seekTo(startPlayingFromPosition, 0)
                repeatMode = queueService.repeatMode.first().toExoPlayerRepeatMode()
                playbackParameters = preferenceProvider.playbackParams.value
                    .toCorrectedParams()
                    .toExoPlayerPlaybackParameters()
                play()
            }
        }
    }
}