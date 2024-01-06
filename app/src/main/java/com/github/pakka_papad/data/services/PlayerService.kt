package com.github.pakka_papad.data.services

import android.content.Context
import android.content.Intent
import android.os.Build
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.player.ZenPlayer

interface PlayerService {
    fun startServiceIfNotRunning(songs: List<Song>, startPlayingFromPosition: Int)
}

class PlayerServiceImpl(
    private val context: Context,
): PlayerService {

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun startServiceIfNotRunning(songs: List<Song>, startPlayingFromPosition: Int) {
        if (ZenPlayer.isRunning.get()) return
        val intent = Intent(context, ZenPlayer::class.java)
        intent.putExtra("locations", songs.map { it.location }.toTypedArray())
        intent.putExtra("startPosition", startPlayingFromPosition)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}