package com.github.pakka_papad.data.services

import com.github.pakka_papad.data.daos.PlaylistDao
import com.github.pakka_papad.data.music.Playlist
import com.github.pakka_papad.data.music.PlaylistExceptId
import com.github.pakka_papad.data.music.PlaylistSongCrossRef
import com.github.pakka_papad.data.music.PlaylistWithSongCount
import com.github.pakka_papad.data.music.PlaylistWithSongs
import com.github.pakka_papad.data.thumbnails.ThumbnailDao
import kotlinx.coroutines.flow.Flow

interface PlaylistService {
    val playlists: Flow<List<PlaylistWithSongCount>>

    fun getPlaylistWithSongsById(playlistId: Long): Flow<PlaylistWithSongs?>

    suspend fun createPlaylist(name: String): Boolean
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun updatePlaylist(updatedPlaylist: Playlist)

    suspend fun addSongsToPlaylist(songLocations: List<String>, playlistId: Long)
    suspend fun removeSongsFromPlaylist(songLocations: List<String>, playlistId: Long)
}

class PlaylistServiceImpl(
    private val playlistDao: PlaylistDao,
    private val thumbnailDao: ThumbnailDao,
): PlaylistService {
    override val playlists: Flow<List<PlaylistWithSongCount>>
        = playlistDao.getAllPlaylistWithSongCount()

    override fun getPlaylistWithSongsById(playlistId: Long): Flow<PlaylistWithSongs?> {
        return playlistDao.getPlaylistWithSongs(playlistId)
    }

    override suspend fun createPlaylist(name: String): Boolean {
        if (name.isBlank()) return false
        val playlist = PlaylistExceptId(
            playlistName = name.trim(),
            createdAt = System.currentTimeMillis()
        )
        playlistDao.insertPlaylist(playlist)
        return true
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        val playlist = playlistDao.getPlaylist(playlistId)
        playlistDao.deletePlaylist(playlistId)
        if (playlist?.artUri == null) return
        thumbnailDao.markDelete(playlist.artUri)
    }

    override suspend fun updatePlaylist(updatedPlaylist: Playlist) {
        playlistDao.updatePlaylist(updatedPlaylist)
    }

    override suspend fun addSongsToPlaylist(songLocations: List<String>, playlistId: Long) {
        playlistDao.insertPlaylistSongCrossRef(
            songLocations.map {
                PlaylistSongCrossRef(
                    playlistId = playlistId,
                    location = it
                )
            }
        )
    }

    override suspend fun removeSongsFromPlaylist(songLocations: List<String>, playlistId: Long) {
        songLocations.forEach {
            playlistDao.deletePlaylistSongCrossRef(
                PlaylistSongCrossRef(
                    playlistId = playlistId,
                    location = it
                )
            )
        }
    }
}