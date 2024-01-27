package com.github.pakka_papad.player

import androidx.datastore.core.DataStore
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import com.github.pakka_papad.data.QueueState
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.data.services.QueueService
import com.github.pakka_papad.data.services.SongService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@ServiceScoped
class SessionCallback @Inject constructor(
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