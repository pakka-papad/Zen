package tech.zemn.mobile.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import tech.zemn.mobile.R

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
        val requiredChannels = listOf(
            NotificationChannel(
                RUNNING_SCAN,
                "Scan",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
    }

    private fun createNotificationChannels() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannels(requiredChannels)
    }

    fun sendScanningNotification() {
        val notification = NotificationCompat.Builder(context, RUNNING_SCAN).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle("Scanning")
            setContentText("Looking for music \uD83E\uDDD0")
            setOngoing(true)
            priority = NotificationCompat.PRIORITY_HIGH
            setProgress(0,0,true)
            setSilent(true)
        }.build()
        with(NotificationManagerCompat.from(context)){
            notify(10,notification)
        }
    }

    fun removeScanningNotification() {
        with(NotificationManagerCompat.from(context)){
            cancel(10)
        }
    }
}