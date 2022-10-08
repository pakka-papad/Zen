package tech.zemn.mobile.data.notification

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
import tech.zemn.mobile.Constants
import tech.zemn.mobile.R
import tech.zemn.mobile.player.ZemnBroadcastReceiver

class ZemnNotificationManager(
    val context: Context
) {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels()
        }
    }

    companion object {
        const val RUNNING_SCAN = "running_scan"
        const val PLAYER_SERVICE = "zemn_player"
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
        IconCompat.createWithResource(context,R.drawable.ic_baseline_skip_previous_24),
        "Previous",
        PendingIntent.getBroadcast(
            context, ZemnBroadcastReceiver.PREVIOUS_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZemnBroadcastReceiver.AUDIO_CONTROL,
                ZemnBroadcastReceiver.ZEMN_PLAYER_PREVIOUS
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()
    private val nextAction = NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context,R.drawable.ic_baseline_skip_next_24),
        "Next",
        PendingIntent.getBroadcast(
            context, ZemnBroadcastReceiver.NEXT_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZemnBroadcastReceiver.AUDIO_CONTROL,
                ZemnBroadcastReceiver.ZEMN_PLAYER_NEXT
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()
    private val pauseAction = NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context,R.drawable.ic_baseline_pause_24),
        "Next",
        PendingIntent.getBroadcast(
            context, ZemnBroadcastReceiver.PAUSE_PLAY_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZemnBroadcastReceiver.AUDIO_CONTROL,
                ZemnBroadcastReceiver.ZEMN_PLAYER_PAUSE_PLAY
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()
    private val playAction = NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context,R.drawable.ic_baseline_play_arrow_24),
        "Next",
        PendingIntent.getBroadcast(
            context, ZemnBroadcastReceiver.PAUSE_PLAY_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZemnBroadcastReceiver.AUDIO_CONTROL,
                ZemnBroadcastReceiver.ZEMN_PLAYER_PAUSE_PLAY
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()

    fun getPlayerNotification(
        session: MediaSessionCompat,
        showPreviousButton: Boolean = true,
        showPlayButton: Boolean,
        showNextButton: Boolean = true
    ): Notification {
        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(session.sessionToken)
        return NotificationCompat.Builder(context, PLAYER_SERVICE).apply {
            setSmallIcon(R.mipmap.ic_notification)
            setContentTitle("Now Playing")
            setContentText("")
            setOngoing(true)
            priority = NotificationCompat.PRIORITY_MAX
            setSilent(true)
            setStyle(mediaStyle)
            if(showPreviousButton) addAction(previousAction)
            addAction(if (showPlayButton) playAction else pauseAction)
            if (showNextButton) addAction(nextAction)
        }.build()
    }
}