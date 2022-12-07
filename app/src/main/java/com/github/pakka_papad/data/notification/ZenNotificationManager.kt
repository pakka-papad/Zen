package com.github.pakka_papad.data.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.github.pakka_papad.Constants
import com.github.pakka_papad.MainActivity
import com.github.pakka_papad.R
import com.github.pakka_papad.player.ZenBroadcastReceiver

class ZenNotificationManager(
    val context: Context
) {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels()
        }
    }

    companion object {
        const val RUNNING_SCAN = "running_scan"
        const val PLAYER_SERVICE = "zen_player"
        const val SCANNING_NOTIFICATION_ID = 10
        const val PLAYER_NOTIFICATION_ID = 12
    }

    private fun createNotificationChannels() {
        val requiredChannels = listOf(
            NotificationChannel(
                RUNNING_SCAN,
                "Scan",
                NotificationManager.IMPORTANCE_HIGH
            ),
            NotificationChannel(
                PLAYER_SERVICE,
                "Player",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannels(requiredChannels)
    }

    fun sendScanningNotification() {
        val notification = NotificationCompat.Builder(context, RUNNING_SCAN).apply {
            setSmallIcon(R.mipmap.ic_notification)
            setContentTitle("Scanning")
            setContentText("Looking for music \uD83E\uDDD0")
            setOngoing(true)
            priority = NotificationCompat.PRIORITY_HIGH
            setProgress(0, 0, true)
            setSilent(true)
        }.build()
        with(NotificationManagerCompat.from(context)) {
            notify(SCANNING_NOTIFICATION_ID, notification)
        }
    }

    fun removeScanningNotification() {
        with(NotificationManagerCompat.from(context)) {
            cancel(SCANNING_NOTIFICATION_ID)
        }
    }

    private val previousAction = NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context,R.drawable.ic_baseline_skip_previous_40),
        "Previous",
        PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.PREVIOUS_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_PREVIOUS
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()
    private val nextAction = NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context,R.drawable.ic_baseline_skip_next_40),
        "Next",
        PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.NEXT_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_NEXT
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()
    private val pauseAction = NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context,R.drawable.ic_baseline_pause_40),
        "Pause",
        PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.PAUSE_PLAY_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_PAUSE_PLAY
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()
    private val playAction = NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context,R.drawable.ic_baseline_play_arrow_40),
        "Play",
        PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.PAUSE_PLAY_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_PAUSE_PLAY
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()
    private val outlinedLikeAction = NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context,R.drawable.ic_baseline_favorite_border_24),
        "Like",
        PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.LIKE_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_LIKE
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()
    private val filledLikeAction = NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context,R.drawable.ic_baseline_favorite_24),
        "Unlike",
        PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.LIKE_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_LIKE
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()
    private val cancelAction = NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context,R.drawable.ic_baseline_close_30),
        "Close",
        PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.CANCEL_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_CANCEL
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()
    private val activityIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context,MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    fun getPlayerNotification(
        session: MediaSessionCompat,
        showPlayButton: Boolean,
        isLiked: Boolean,
    ): Notification {
        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(1,2,3)
            .setMediaSession(session.sessionToken)
        return NotificationCompat.Builder(context, PLAYER_SERVICE).apply {
            setSmallIcon(R.mipmap.ic_notification)
            setContentTitle("Now Playing")
            setContentText("")
            setOngoing(true)
            priority = NotificationCompat.PRIORITY_MAX
            setSilent(true)
            setStyle(mediaStyle)
            addAction(if (isLiked) filledLikeAction else outlinedLikeAction)
            addAction(previousAction)
            addAction(if (showPlayButton) playAction else pauseAction)
            addAction(nextAction)
            addAction(cancelAction)
            setContentIntent(activityIntent)  // use with android:launchMode="singleTask" in manifest
        }.build()
    }
}