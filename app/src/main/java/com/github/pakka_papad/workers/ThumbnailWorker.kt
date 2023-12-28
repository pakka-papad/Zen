package com.github.pakka_papad.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.pakka_papad.data.music.Playlist
import com.github.pakka_papad.data.music.PlaylistWithSongCount
import com.github.pakka_papad.data.services.PlaylistService
import com.github.pakka_papad.data.services.ThumbnailService
import com.github.pakka_papad.data.thumbnails.Thumbnail
import com.github.pakka_papad.data.thumbnails.ThumbnailWithoutId
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days

@HiltWorker
class ThumbnailWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
    private val thumbnailService: ThumbnailService,
    private val playlistService: PlaylistService,
): CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        createPlaylistThumbnails()
        return Result.success()
    }

    private val sevenDays = 7.days.inWholeMilliseconds

    private fun isThumbnailGood(
        thumbnail: Thumbnail?,
        playlist: PlaylistWithSongCount,
    ): Boolean {
        return ( thumbnail != null &&
                (thumbnail.artCount >= 9 || thumbnail.artCount == playlist.count) &&
                thumbnail.lastUpdatedOn + sevenDays >= System.currentTimeMillis()
            )
    }

    private suspend fun createPlaylistThumbnails() = coroutineScope {
        val playlists = playlistService.playlists.first()
        val jobs = mutableListOf<Job>()
        playlists.forEach { playlist ->
            launch {
                val existingThumbnail = if (playlist.artUri == null) null else
                    thumbnailService.getThumbnailByPath(playlist.artUri)
                if (isThumbnailGood(existingThumbnail, playlist)) {
                    return@launch
                }
                val songs = playlistService.getPlaylistWithSongsById(playlist.playlistId)
                    .first()?.songs ?: return@launch
                val uri = thumbnailService
                    .createThumbnailImage(songs.map { it.artUri }) ?: return@launch
                val newThumbnail = ThumbnailWithoutId(
                    location = uri,
                    lastUpdatedOn = System.currentTimeMillis(),
                    artCount = minOf(9, songs.size),
                    deleteThis = false
                )
                thumbnailService.insert(newThumbnail)
                val updatedPlaylist = Playlist(
                    playlistId = playlist.playlistId,
                    playlistName = playlist.playlistName,
                    createdAt = playlist.createdAt,
                    artUri = uri,
                )
                playlistService.updatePlaylist(updatedPlaylist)
                if (existingThumbnail != null) {
                    thumbnailService.markDelete(existingThumbnail.location)
                }
            }.also {
                jobs.add(it)
            }
        }
        jobs.joinAll()
    }

}