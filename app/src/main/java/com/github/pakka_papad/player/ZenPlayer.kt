package com.github.pakka_papad.player

import android.app.NotificationManager
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.PlaybackStatsListener
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.DataManager
import com.github.pakka_papad.data.ZenCrashReporter
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.data.notification.ZenNotificationManager
import com.github.pakka_papad.data.services.AnalyticsService
import com.github.pakka_papad.data.services.QueueService
import com.github.pakka_papad.data.services.SongService
import com.github.pakka_papad.toCorrectedParams
import com.github.pakka_papad.toExoPlayerPlaybackParameters
import com.github.pakka_papad.widgets.WidgetBroadcast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@UnstableApi @AndroidEntryPoint
class ZenPlayer : Service(), QueueService.Listener, ZenBroadcastReceiver.Callback {

    @Inject
    lateinit var notificationManager: ZenNotificationManager

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var queueService: QueueService

    @Inject
    lateinit var songService: SongService

    @Inject
    lateinit var analyticsService: AnalyticsService

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var crashReporter: ZenCrashReporter

    @Inject
    lateinit var preferencesProvider: ZenPreferenceProvider

    private var broadcastReceiver: ZenBroadcastReceiver? = null

    private var systemNotificationManager: NotificationManager? = null

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Default)

    private val playTimeThresholdMs = 10.seconds.inWholeMilliseconds

    private val playbackStatsListener = PlaybackStatsListener(false) { eventTime, playbackStats ->
        if (playbackStats.totalPlayTimeMs < playTimeThresholdMs) return@PlaybackStatsListener
        val window = eventTime.timeline.getWindow(eventTime.windowIndex, Timeline.Window())
        try {
            window.mediaItem.localConfiguration?.tag?.let {
//                dataManager.addPlayHistory(it as String, playbackStats.totalPlayTimeMs)
                analyticsService.logSongPlay(it as String, playbackStats.totalPlayTimeMs)
            }
        } catch (_ : Exception){

        }
    }

    companion object {
        const val MEDIA_SESSION = "media_session"
        val isRunning = AtomicBoolean(false)
    }

    private lateinit var mediaSession: MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder? = null

    private val exoPlayerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            try {
                queueService.setCurrentSong(exoPlayer.currentMediaItemIndex)
                queueService.getSongAtIndex(exoPlayer.currentMediaItemIndex)?.let { song ->
                    val broadcast = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                        putExtra(WidgetBroadcast.WIDGET_BROADCAST, WidgetBroadcast.SONG_CHANGED)
                        putExtra("imageUri", song.artUri)
                        putExtra("title", song.title)
                        putExtra("artist", song.artist)
                        putExtra("album", song.album)
                    }
                    this@ZenPlayer.applicationContext.sendBroadcast(broadcast)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
            updateMediaSessionState()
            updateMediaSessionMetadata()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            val broadcast = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                putExtra(WidgetBroadcast.WIDGET_BROADCAST, WidgetBroadcast.IS_PLAYING_CHANGED)
                putExtra("isPlaying", isPlaying)
            }
            this@ZenPlayer.applicationContext.sendBroadcast(broadcast)
            updateMediaSessionState()
            updateMediaSessionMetadata()
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            crashReporter.logException(error)
            if (error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND){
                dataManager.cleanData()
//                showToast("Could not find the song ${dataManager.currentSong.value?.title ?: ""} at the specified path")
                onBroadcastCancel()
            }
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            super.onPlayerErrorChanged(error)
            crashReporter.logException(error)
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
        broadcastReceiver = ZenBroadcastReceiver()
        mediaSession = MediaSessionCompat(this, MEDIA_SESSION)
        systemNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        dataManager.setPlayerRunning(this)
        isRunning.set(true)
        queueService.addListener(this)
        if (intent == null) Toast.makeText(this, "null intent", Toast.LENGTH_SHORT).show()
        intent?.let {
            val locations = it.getStringArrayExtra("locations") ?: return@let
            val startPosition = it.getIntExtra("startPosition", 0)
            if (locations.isEmpty()) return@let
            val mediaItems = locations.map { location ->
                MediaItem.Builder().apply {
                    setUri(Uri.fromFile(File(location)))
                    setTag(location)
                }.build()
            }
            setQueue(mediaItems, startPosition)
        }

        IntentFilter(Constants.PACKAGE_NAME).also {
            registerReceiver(broadcastReceiver, it)
        }
        broadcastReceiver?.startListening(this)
        mediaSession.setCallback(mediaSessionCallback)
        exoPlayer.addListener(exoPlayerListener)
        exoPlayer.addAnalyticsListener(playbackStatsListener)

        startForeground(
            ZenNotificationManager.PLAYER_NOTIFICATION_ID,
            notificationManager.getPlayerNotification(
                session = mediaSession,
                showPlayButton = false,
                isLiked = queueService.getSongAtIndex(exoPlayer.currentMediaItemIndex)?.favourite ?: false
            )
        )

        scope.launch {
            preferencesProvider.playbackParams.collect {
                updateMediaSessionState()
                val params = it.toCorrectedParams().toExoPlayerPlaybackParameters()
                withContext(Dispatchers.Main){
                    exoPlayer.playbackParameters = params
                }
            }
        }
        scope.launch {
            queueService.repeatMode.collect {
                withContext(Dispatchers.Main) { exoPlayer.repeatMode = it.toExoPlayerRepeatMode() }
            }
        }

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
        exoPlayer.removeAnalyticsListener(playbackStatsListener)
        mediaSession.release()
//        dataManager.stopPlayerRunning()
        isRunning.set(false)
        queueService.clearQueue()
        queueService.removeListener(this)
        broadcastReceiver?.stopListening()
        systemNotificationManager?.cancel(ZenNotificationManager.PLAYER_NOTIFICATION_ID)
        scope.cancel()
        job.cancel()
        systemNotificationManager = null
        broadcastReceiver = null
        val broadcast = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
            putExtra(WidgetBroadcast.WIDGET_BROADCAST, WidgetBroadcast.SONG_CHANGED)
            putExtra("imageUri", "")
            putExtra("title", "")
            putExtra("artist", "")
            putExtra("album", "")
        }
        applicationContext.sendBroadcast(broadcast)
    }

    private fun updateMediaSessionMetadata() {
        scope.launch {
            var currentSong: Song? = null
            withContext(Dispatchers.Main) {
                currentSong = queueService.getSongAtIndex(exoPlayer.currentMediaItemIndex)
            }
            if (currentSong == null) return@launch
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder().apply {
                    putString(
                        MediaMetadataCompat.METADATA_KEY_TITLE,
                        currentSong!!.title
                    )
                    putString(
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        currentSong!!.artist
                    )
                    putString(
                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                        currentSong!!.artUri
                    )
                    putLong(
                        MediaMetadataCompat.METADATA_KEY_DURATION,
                        currentSong!!.durationMillis
                    )
                }.build()
            )
            delay(100)
            withContext(Dispatchers.Main) {
                systemNotificationManager?.notify(
                    ZenNotificationManager.PLAYER_NOTIFICATION_ID,
                    notificationManager.getPlayerNotification(
                        session = mediaSession,
                        showPlayButton = !exoPlayer.isPlaying,
                        isLiked = queueService.getSongAtIndex(exoPlayer.currentMediaItemIndex)?.favourite ?: false
                    )
                )
            }
        }
    }

    private fun updateMediaSessionState() {
        scope.launch {
            delay(100)
            val speed = preferencesProvider.playbackParams.value.playbackSpeed
            withContext(Dispatchers.Main) {
                mediaSession.setPlaybackState(
                    PlaybackStateCompat.Builder().apply {
                        setState(
                            if (exoPlayer.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                            exoPlayer.currentPosition,
                            if (speed < 1 || speed > 200) 1f else speed.toFloat()/100,
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

//    @Synchronized
//    override fun setQueue(newQueue: List<Song>, startPlayingFromIndex: Int) {
//        scope.launch {
//            val mediaItems = newQueue.map {
//                MediaItem.Builder().apply {
//                    setUri(Uri.fromFile(File(it.location)))
//                    setTag(it.location)
//                }.build()
//            }
//            withContext(Dispatchers.Main){
//                exoPlayer.stop()
//                exoPlayer.clearMediaItems()
//                exoPlayer.addMediaItems(mediaItems)
//                exoPlayer.prepare()
//                exoPlayer.seekTo(startPlayingFromIndex,0)
//                exoPlayer.repeatMode = dataManager.repeatMode.value.toExoPlayerRepeatMode()
//                exoPlayer.playbackParameters = preferencesProvider.playbackParams.value
//                    .toCorrectedParams()
//                    .toExoPlayerPlaybackParameters()
//                exoPlayer.play()
//            }
//            updateMediaSessionState()
//            updateMediaSessionMetadata()
//        }
//    }
//
//    @Synchronized
//    override fun addToQueue(song: Song) {
//        exoPlayer.addMediaItem(
//            MediaItem.Builder().apply {
//                setUri(Uri.fromFile(File(song.location)))
//                setTag(song.location)
//            }.build()
//        )
//    }
//
//    @Synchronized
//    override fun updateNotification() {
//        updateMediaSessionState()
//        updateMediaSessionMetadata()
//    }

    private fun setQueue(mediaItems: List<MediaItem>, startPosition: Int){
        scope.launch {
            val repeatMode = queueService.repeatMode.first()
            withContext(Dispatchers.Main){
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
                exoPlayer.addMediaItems(mediaItems)
                exoPlayer.prepare()
                exoPlayer.seekTo(startPosition,0)
                exoPlayer.repeatMode = repeatMode.toExoPlayerRepeatMode()
                exoPlayer.playbackParameters = preferencesProvider.playbackParams.value
                    .toCorrectedParams()
                    .toExoPlayerPlaybackParameters()
                exoPlayer.play()
            }
            updateMediaSessionState()
            updateMediaSessionMetadata()
        }
    }

    override fun onAppend(song: Song) {
        exoPlayer.addMediaItem(
            MediaItem.Builder().apply {
                setUri(Uri.fromFile(File(song.location)))
                setTag(song.location)
            }.build()
        )
    }

    override fun onAppend(songs: List<Song>) {
        exoPlayer.addMediaItems(
            songs.map {
                MediaItem.Builder().apply {
                    setUri(Uri.fromFile(File(it.location)))
                    setTag(it.location)
                }.build()
            }
        )
    }

    override fun onUpdate(updatedSong: Song, position: Int) {
        scope.launch {
            val performUpdate = withContext(Dispatchers.Main) {
                exoPlayer.currentMediaItemIndex == position
            }
            if (!performUpdate) return@launch
            updateMediaSessionState()
            updateMediaSessionMetadata()
        }
    }

    override fun onMove(from: Int, to: Int) {
        exoPlayer.moveMediaItem(from, to)
    }

    override fun onClear() {

    }

    override fun onSetQueue(songs: List<Song>, startPlayingFromPosition: Int) {
        val mediaItems = songs.map {
            MediaItem.Builder().apply {
                setUri(Uri.fromFile(File(it.location)))
                setTag(it.location)
            }.build()
        }
        setQueue(mediaItems, startPlayingFromPosition)
    }

    /**
     * Called when user clicks play/pause button in notification.
     * Player.Listener onIsPlayingChanged gets called.
     */
    override fun onBroadcastPausePlay() {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    /**
     * Called when user clicks next button in notification.
     * If we have next song in queue we skip to it.
     * Player.Listener onMediaItemTransition gets called.
     */
    override fun onBroadcastNext() {
        if (!exoPlayer.hasNextMediaItem()) {
            showToast("No next song in queue")
            return
        }
        exoPlayer.seekToNextMediaItem()
    }

    /**
     * Called when user clicks previous button in notification.
     * If we have previous song in queue we skip to it.
     * Player.Listener onMediaItemTransition gets called.
     */
    override fun onBroadcastPrevious() {
        if (!exoPlayer.hasPreviousMediaItem()) {
            showToast("No previous song in queue")
            return
        }
        exoPlayer.seekToPreviousMediaItem()
    }

    /**
     * Called when user clicks on like icon (filled and outlined both)
     * This fetches the current song, toggles the favourite and passes the updated song to DataManager
     * DataManager then calls updateNotification of DataManager.Callback
     */
    override fun onBroadcastLike() {
        val currentSong = queueService.getSongAtIndex(exoPlayer.currentMediaItemIndex) ?: return
        val updatedSong = currentSong.copy(favourite = !currentSong.favourite)
        scope.launch {
//            onUpdateCurrentSong()
            queueService.update(updatedSong)
            songService.updateSong(updatedSong)
        }
    }

    /**
     * Called when user clicks close button in notification
     * This stops the service and onDestroy is called
     */
    override fun onBroadcastCancel() {
        // Deprecated in api level 33
//        stopForeground(true)
        stopSelf()
    }
}