package tech.zemn.mobile.player

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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import tech.zemn.mobile.Constants
import tech.zemn.mobile.data.DataManager
import tech.zemn.mobile.data.music.Song
import tech.zemn.mobile.data.notification.ZemnNotificationManager
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ZemnPlayer : Service(), DataManager.Callback, ZemnBroadcastReceiver.Callback {

    @Inject
    lateinit var notificationManager: ZemnNotificationManager

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var broadcastReceiver: ZemnBroadcastReceiver

    private lateinit var systemNotificationManager: NotificationManager

    companion object {
        const val MEDIA_SESSION = "media_session"
    }

    private lateinit var mediaSession: MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder? = null
    private val queue = ArrayList<Song>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaSession = MediaSessionCompat(this, MEDIA_SESSION)
        systemNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        dataManager.setPlayerRunning(this)
        IntentFilter(Constants.PACKAGE_NAME).also {
            registerReceiver(broadcastReceiver, it)
        }
        broadcastReceiver.startListening(this)

//        mediaSession.setCallback(
//            object : MediaSessionCompat.Callback() {
//                override fun onPlay() {
//                    super.onPlay()
//                    Timber.d("on play")
//                }
//
//                override fun onPause() {
//                    super.onPause()
//                    Timber.d("on pause")
//                }
//
//                override fun onSkipToNext() {
//                    super.onSkipToNext()
//                    Timber.d("on next")
//                }
//
//                override fun onSkipToPrevious() {
//                    super.onSkipToPrevious()
//                    Timber.d("on previous")
//                }
//            }
//        )
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)

                    updateMediaSessionMetadata()
                    updateMediaSessionState(
                        showPrevious = false,
                        showNext = false,
                    )
                    try {
                        dataManager.updateCurrentSong(
                            queue[exoPlayer.currentMediaItemIndex]
                        )
                    } catch (e: Exception){
                        Timber.e(e)
                    }
                }
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    updateMediaSessionState(
                        showPrevious = false,
                        showNext = false
                    )
                }
            }
        )

        startForeground(
            ZemnNotificationManager.PLAYER_NOTIFICATION_ID,
            notificationManager.getPlayerNotification(
                session = mediaSession,
                showPreviousButton = false,
                showPlayButton = false,
                showNextButton = false,
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

    private fun stopService() {
        unregisterReceiver(broadcastReceiver)
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        dataManager.stopPlayerRunning()
        broadcastReceiver.stopListening()
        systemNotificationManager.cancel(ZemnNotificationManager.PLAYER_NOTIFICATION_ID)
        stopForeground(true)
        stopSelf()
    }

    private fun updateMediaSessionMetadata(){
        val currentSong = try {
            queue[exoPlayer.currentMediaItemIndex]
        } catch (e: Exception) {
            null
        } ?: return

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
        systemNotificationManager.notify(
            ZemnNotificationManager.PLAYER_NOTIFICATION_ID,
            notificationManager.getPlayerNotification(
                session = mediaSession,
                showPreviousButton = false,
                showPlayButton = !exoPlayer.isPlaying,
                showNextButton = false,
            )
        )
    }

    private fun updateMediaSessionState(
        showPrevious: Boolean = false,
        showNext: Boolean = false,
    ){
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder().apply {
                setState(
                    if (exoPlayer.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                    exoPlayer.currentPosition,
                    1f,
                )
                setActions(if (exoPlayer.isPlaying) PlaybackStateCompat.ACTION_PAUSE else PlaybackStateCompat.ACTION_PLAY)
                if (showPrevious) setActions(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                if (showNext) setActions(PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
            }.build()
        )
    }

    @Synchronized
    override fun setQueue(newQueue: List<Song>, startPlayingFromIndex: Int) {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        val mediaItems = newQueue.map {
            MediaItem.fromUri(it.location)
        }
        queue.clear()
        queue.addAll(newQueue)
        exoPlayer.addMediaItems(mediaItems)
        exoPlayer.prepare()
        repeat(startPlayingFromIndex){
            exoPlayer.seekToNextMediaItem()
        }
        exoPlayer.play()

        updateMediaSessionMetadata()

        updateMediaSessionState(
            showPrevious = false,
            showNext = false,
        )

        systemNotificationManager.notify(
            ZemnNotificationManager.PLAYER_NOTIFICATION_ID,
            notificationManager.getPlayerNotification(
                session = mediaSession,
                showPreviousButton = false,
                showPlayButton = false,
                showNextButton = false,
            )
        )

    }

    @Synchronized
    override fun addToQueue(song: Song) {
        queue.add(song)
        exoPlayer.addMediaItem(MediaItem.fromUri(song.location))
        updateMediaSessionMetadata()
        updateMediaSessionState(
            showPrevious = false,
            showNext = false,
        )
        systemNotificationManager.notify(
            ZemnNotificationManager.PLAYER_NOTIFICATION_ID,
            notificationManager.getPlayerNotification(
                session = mediaSession,
                showPreviousButton = false,
                showPlayButton = false,
                showNextButton = false,
            )
        )
    }

    override fun onBroadcastPausePlay() {
        Timber.d("broadcast received")
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
        systemNotificationManager.notify(
            ZemnNotificationManager.PLAYER_NOTIFICATION_ID,
            notificationManager.getPlayerNotification(
                session = mediaSession,
                showPreviousButton = false,
                showPlayButton = !exoPlayer.isPlaying,
                showNextButton = false,
            )
        )
        updateMediaSessionState(
            showPrevious = false,
            showNext = false,
        )
    }

    override fun onBroadcastNext() {
        exoPlayer.seekToNextMediaItem()
    }

    override fun onBroadcastPrevious() {
        exoPlayer.seekToPreviousMediaItem()
    }

}