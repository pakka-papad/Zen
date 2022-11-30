package com.github.pakka_papad.player

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.DataManager
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.data.notification.ZenNotificationManager
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ZenPlayer : Service(), DataManager.Callback, ZenBroadcastReceiver.Callback {

    @Inject
    lateinit var notificationManager: ZenNotificationManager

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var broadcastReceiver: ZenBroadcastReceiver

    private lateinit var systemNotificationManager: NotificationManager

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Default)

    companion object {
        const val MEDIA_SESSION = "media_session"
    }

    private lateinit var mediaSession: MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder? = null

    private val exoPlayerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            try {
                dataManager.updateCurrentSong(exoPlayer.currentMediaItemIndex)
            } catch (e: Exception) {
                Timber.e(e)
            }
            updateMediaSessionState()
            updateMediaSessionMetadata()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            updateMediaSessionState()
            updateMediaSessionMetadata()
        }
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            onBroadcastPausePlay()
        }

        override fun onPause() {
            super.onPause()
            onBroadcastPausePlay()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            onBroadcastNext()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            onBroadcastPrevious()
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            exoPlayer.seekTo(pos)
            updateMediaSessionState()
            updateMediaSessionMetadata()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaSession = MediaSessionCompat(this, MEDIA_SESSION)
        systemNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        dataManager.setPlayerRunning(this)
        IntentFilter(Constants.PACKAGE_NAME).also {
            registerReceiver(broadcastReceiver, it)
        }
        broadcastReceiver.startListening(this)

        mediaSession.setCallback(mediaSessionCallback)
        exoPlayer.addListener(exoPlayerListener)

        startForeground(
            ZenNotificationManager.PLAYER_NOTIFICATION_ID,
            notificationManager.getPlayerNotification(
                session = mediaSession,
                showPreviousButton = true,
                showPlayButton = false,
                showNextButton = true,
            )
        )

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = true

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun stopService() {
        unregisterReceiver(broadcastReceiver)
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.removeListener(exoPlayerListener)
        dataManager.stopPlayerRunning()
        broadcastReceiver.stopListening()
        systemNotificationManager.cancel(ZenNotificationManager.PLAYER_NOTIFICATION_ID)
        stopForeground(true)
        stopSelf()
    }

    private fun updateMediaSessionMetadata() {
        val currentSong = dataManager.getSongAtIndex(exoPlayer.currentMediaItemIndex) ?: return

        val extractor = MediaMetadataRetriever()
        extractor.setDataSource(currentSong.location)

        mediaSession.setMetadata(
            MediaMetadataCompat.Builder().apply {
                putString(
                    MediaMetadataCompat.METADATA_KEY_TITLE,
                    currentSong.title
                )
                putString(
                    MediaMetadataCompat.METADATA_KEY_ARTIST,
                    currentSong.artist
                )
                if (extractor.embeddedPicture != null) {
                    putBitmap(
                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                        BitmapFactory.decodeByteArray(
                            extractor.embeddedPicture, 0,
                            extractor.embeddedPicture!!.size
                        )
                    )
                }
                putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    currentSong.durationMillis
                )
            }.build()
        )

        scope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                systemNotificationManager.notify(
                    ZenNotificationManager.PLAYER_NOTIFICATION_ID,
                    notificationManager.getPlayerNotification(
                        session = mediaSession,
                        showPreviousButton = true,
                        showPlayButton = !exoPlayer.isPlaying,
                        showNextButton = true,
                    )
                )
            }
        }
    }

    private fun updateMediaSessionState() {
        scope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                mediaSession.setPlaybackState(
                    PlaybackStateCompat.Builder().apply {
                        setState(
                            if (exoPlayer.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                            exoPlayer.currentPosition,
                            1f,
                        )
                        setActions(
                            (if (exoPlayer.isPlaying) PlaybackStateCompat.ACTION_PAUSE else PlaybackStateCompat.ACTION_PLAY)
                                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                                    or PlaybackStateCompat.ACTION_SEEK_TO
                        )
                    }.build()
                )
            }
        }
    }

    @Synchronized
    override fun setQueue(newQueue: List<Song>, startPlayingFromIndex: Int) {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        val mediaItems = newQueue.map {
            MediaItem.fromUri(it.location)
        }
        exoPlayer.addMediaItems(mediaItems)
        exoPlayer.prepare()
        exoPlayer.seekTo(startPlayingFromIndex,0)
        exoPlayer.play()
        updateMediaSessionState()
        updateMediaSessionMetadata()
    }

    @Synchronized
    override fun addToQueue(song: Song) {
        exoPlayer.addMediaItem(MediaItem.fromUri(song.location))
    }

    override fun onBroadcastPausePlay() {
        Timber.d("broadcast received")
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
        updateMediaSessionState()
        updateMediaSessionMetadata()
    }

    override fun onBroadcastNext() {
        if (!exoPlayer.hasNextMediaItem()) {
            showToast("No next song in queue")
            return
        }
        exoPlayer.seekToNextMediaItem()
        // not needed as below functions will be triggered in onMediaItemTransition
//        updateMediaSessionState()
//        updateMediaSessionMetadata()
    }

    override fun onBroadcastPrevious() {
        if (!exoPlayer.hasPreviousMediaItem()) {
            showToast("No previous song in queue")
            return
        }
        exoPlayer.seekToPreviousMediaItem()
        // not needed as below functions will be triggered in onMediaItemTransition
//        updateMediaSessionState()
//        updateMediaSessionMetadata()
    }

}