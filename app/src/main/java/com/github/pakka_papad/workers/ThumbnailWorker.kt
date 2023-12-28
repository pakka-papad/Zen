package com.github.pakka_papad.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.pakka_papad.data.services.PlaylistService
import com.github.pakka_papad.data.services.ThumbnailService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

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

    private suspend fun createPlaylistThumbnails() {
        val playlists = playlistService.playlists.first()
        playlists.forEach {
            coroutineScope {
                // create and save thumbnails
            }
        }
    }

}