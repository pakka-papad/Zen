package com.github.pakka_papad.player

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.QueueState
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.data.services.QueueService
import com.github.pakka_papad.data.services.SongService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@ServiceScoped
class SessionCallback @Inject constructor(
    @ApplicationContext context: Context,
    private val queueService: QueueService,
    private val songService: SongService,
    private val scope: CoroutineScope,
    private val queueState: DataStore<QueueState>,
): MediaSession.Callback {

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        val availableCommands = connectionResult.availableSessionCommands.buildUpon()
        availableCommands.add(ZenCommandButtons.liked.sessionCommand!!)
        availableCommands.add(ZenCommandButtons.unliked.sessionCommand!!)
        availableCommands.add(ZenCommandButtons.cancel.sessionCommand!!)
        return MediaSession.ConnectionResult.accept(
            availableCommands.build(),
            connectionResult.availablePlayerCommands
        )
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
        super.onPostConnect(session, controller)
        val isLiked = queueService.currentSong.value?.favourite ?: false
        Timber.d("onPostConnect() -> ${session.player.currentMediaItem?.mediaMetadata?.title} isLiked: $isLiked")
        session.setCustomLayout(
            controller,
            listOf(
                if (isLiked) ZenCommandButtons.liked else ZenCommandButtons.unliked,
                ZenCommandButtons.previous,
                ZenCommandButtons.playPause,
                ZenCommandButtons.next,
                ZenCommandButtons.cancel
            )
        )
    }

    private val closeAction =  PendingIntent.getBroadcast(
        context, ZenBroadcastReceiver.CANCEL_ACTION_REQUEST_CODE,
        Intent(Constants.PACKAGE_NAME).putExtra(
            ZenBroadcastReceiver.AUDIO_CONTROL,
            ZenBroadcastReceiver.ZEN_PLAYER_CANCEL
        ),
        PendingIntent.FLAG_IMMUTABLE
    )

    private val likeUnlikeAction = PendingIntent.getBroadcast(
        context, ZenBroadcastReceiver.LIKE_ACTION_REQUEST_CODE,
        Intent(Constants.PACKAGE_NAME).putExtra(
            ZenBroadcastReceiver.AUDIO_CONTROL,
            ZenBroadcastReceiver.ZEN_PLAYER_LIKE
        ),
        PendingIntent.FLAG_IMMUTABLE
    )

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        val result = SettableFuture.create<SessionResult>()
        when(customCommand.customAction) {
            ZenCommands.LIKE, ZenCommands.UNLIKE -> {
                likeUnlikeAction.send()
            }
            ZenCommands.CLOSE -> {
                closeAction.send()
            }
        }
        result.set(SessionResult(SessionResult.RESULT_SUCCESS))
        return result
    }

    @UnstableApi
    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        val result = SettableFuture.create<MediaSession.MediaItemsWithStartPosition>()
        scope.launch {
            val state = queueState.data.first()
            val songs = songService.getSongsFromLocations(state.locationsList)
            val locationMap = buildMap {
                for (song in songs) {
                    put(song.location, song)
                }
            }
            val orderedSongs = buildList {
                for (location in state.locationsList) {
                    if (locationMap.containsKey(location)) {
                        add(locationMap[location]!!)
                    }
                }
            }
            queueService.clearQueue()
            queueService.setQueue(orderedSongs, state.startIndex)
            result.set(
                MediaSession.MediaItemsWithStartPosition(
                    orderedSongs.map(Song::toMediaItem),
                    state.startIndex,
                    state.startPositionMs
                )
            )
        }
        return result
    }

}