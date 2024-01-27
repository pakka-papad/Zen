package com.github.pakka_papad.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.github.pakka_papad.MainActivity
import com.github.pakka_papad.R
import com.github.pakka_papad.data.services.QueueService
import com.google.common.collect.ImmutableList
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import timber.log.Timber
import javax.inject.Inject

@ServiceScoped
@UnstableApi
class NotificationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val queueService: QueueService,
) : MediaNotification.Provider {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels() {
        val requiredChannels = listOf(
            NotificationChannel(
                PLAYER_SERVICE_NOTIFICATION_CHANNEL_ID,
                "Player",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannels(requiredChannels)
    }

    private val activityIntent by lazy {
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val PLAYER_NOTIFICATION_ID = 20
        const val PLAYER_SERVICE_NOTIFICATION_CHANNEL_ID = "zen_player"
    }

    override fun createNotification(
        mediaSession: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        onNotificationChangedCallback: MediaNotification.Provider.Callback
    ): MediaNotification {
        val mediaStyle = MediaStyleNotificationHelper.MediaStyle(mediaSession)
            .setShowActionsInCompactView(1,2,3)

        val builder = NotificationCompat.Builder(context, PLAYER_SERVICE_NOTIFICATION_CHANNEL_ID)
            .apply {
                setSmallIcon(R.mipmap.ic_notification)
                setContentTitle(mediaSession.player.currentMediaItem?.mediaMetadata?.title)
                setContentText(mediaSession.player.currentMediaItem?.mediaMetadata?.artist)
                setOngoing(true)
                priority = NotificationCompat.PRIORITY_MAX
                setSilent(true)
                setStyle(mediaStyle)
                setContentIntent(activityIntent)
            }

        /**
         * queueService.currentSong.value may not have the value correctly updated immediately
         */
        val isLiked = queueService.getSongAtIndex(mediaSession.player.currentMediaItemIndex)
            ?.favourite ?: false

        Timber.d("createNotification() ${mediaSession.player.currentMediaItem?.mediaMetadata?.title} isLiked: $isLiked")

        customLayout.mapIndexed { index, commandButton ->
            when(index) {
                0 -> { // Like or Unlike button
                    actionFactory.createCustomActionFromCustomCommandButton(
                        mediaSession,
                        if (isLiked) ZenCommandButtons.liked else ZenCommandButtons.unliked
                    )
                }
                1, 3 -> { // Previous and Next buttons
                    actionFactory.createMediaAction(
                        mediaSession,
                        IconCompat.createWithResource(context, commandButton.iconResId),
                        commandButton.displayName,
                        commandButton.playerCommand
                    )
                }
                2 -> { // Play or Pause button
                    actionFactory.createMediaAction(
                        mediaSession,
                        IconCompat.createWithResource(
                            context,
                            if (mediaSession.player.isPlaying) R.drawable.ic_baseline_pause_40
                            else R.drawable.ic_baseline_play_arrow_40
                        ),
                        if (mediaSession.player.isPlaying) "Pause"
                        else "Play",
                        commandButton.playerCommand
                    )
                }
                4 -> { // Close button
                    actionFactory
                        .createCustomActionFromCustomCommandButton(mediaSession, commandButton)
                }
                else -> {
                    null
                }
            }
        }.forEach { builder.addAction(it) }

        return MediaNotification(PLAYER_NOTIFICATION_ID, builder.build())
    }

    override fun handleCustomCommand(
        session: MediaSession,
        action: String,
        extras: Bundle
    ): Boolean = false
}