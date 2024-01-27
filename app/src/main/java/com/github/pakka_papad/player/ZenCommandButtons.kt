package com.github.pakka_papad.player

import android.os.Bundle
import androidx.media3.common.Player
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import com.github.pakka_papad.R

object ZenCommandButtons {

    val liked by lazy {
        CommandButton.Builder()
            .apply {
                setSessionCommand(SessionCommand(ZenCommands.UNLIKE, Bundle()))
                setDisplayName("Unlike")
                setIconResId(R.drawable.ic_baseline_favorite_24)
            }.build()
    }

    val unliked by lazy {
        CommandButton.Builder()
            .apply {
                setSessionCommand(SessionCommand(ZenCommands.LIKE, Bundle()))
                setDisplayName("Like")
                setIconResId(R.drawable.ic_baseline_favorite_border_24)
            }.build()
    }

    val previous by lazy {
        CommandButton.Builder()
            .apply {
                setPlayerCommand(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                setDisplayName("Previous")
                setIconResId(R.drawable.ic_baseline_skip_previous_40)
            }.build()
    }


    val playPause by lazy {
        CommandButton.Builder()
            .apply {
                setPlayerCommand(Player.COMMAND_PLAY_PAUSE)
                setDisplayName("Previous")
                setIconResId(R.drawable.ic_baseline_skip_previous_40)
            }.build()
    }

    val next by lazy {
        CommandButton.Builder()
            .apply {
                setPlayerCommand(Player.COMMAND_SEEK_TO_NEXT)
                setDisplayName("Next")
                setIconResId(R.drawable.ic_baseline_skip_next_40)
            }.build()
    }

    val cancel by lazy {
        CommandButton.Builder()
            .apply {
                setSessionCommand(SessionCommand(ZenCommands.CLOSE, Bundle()))
                setDisplayName("Close")
                setIconResId(R.drawable.ic_baseline_close_40)
            }.build()
    }
}