package tech.zemn.mobile.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import tech.zemn.mobile.data.DataManager
import tech.zemn.mobile.data.music.Song
import tech.zemn.mobile.data.notification.ZemnNotificationManager
import javax.inject.Inject

@AndroidEntryPoint
class ZemnPlayer: Service(), DataManager.Callback {

    @Inject lateinit var notificationManager: ZemnNotificationManager
    @Inject lateinit var dataManager: DataManager
    @Inject lateinit var exoPlayer: ExoPlayer

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        dataManager.setPlayerRunning(this)
//        exoPlayer.clearMediaItems()
//        exoPlayer.currentMediaItem
//        exoPlayer.currentLiveOffset
//        exoPlayer.currentPosition

        startForeground(
            ZemnNotificationManager.PLAYER_NOTIFICATION_ID,
            notificationManager.getPlayerNotification("")
        )

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = true

        return START_NOT_STICKY
    }

    private fun stopService(){
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        dataManager.stopPlayerRunning()
        stopForeground(true)
        stopSelf()
    }

    @Synchronized
    override fun updateQueue(newQueue: List<Song>) {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        val mediaItems = newQueue.map {
            MediaItem.fromUri(it.location)
        }
        exoPlayer.addMediaItems(mediaItems)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    @Synchronized
    override fun play() {
        exoPlayer.play()
    }

    @Synchronized
    override fun pause() {
        exoPlayer.pause()
    }

}