package com.github.pakka_papad.data.music

import androidx.room.*
import com.github.pakka_papad.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllSongs(data: List<Song>)

    @Query("SELECT * FROM ${Constants.Tables.SONG_TABLE} ORDER BY title ASC")
    fun getAllSongs(): Flow<List<Song>>

    @Update
    suspend fun updateSong(song: Song)

    @Query("DELETE FROM ${Constants.Tables.SONG_TABLE}")
    suspend fun deleteAllSongs()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllAlbums(data: List<Album>)

    @Transaction
    @Query("SELECT * FROM ${Constants.Tables.ALBUM_TABLE} ORDER BY name ASC")
    fun getAllAlbumsWithSongs(): Flow<List<AlbumWithSongs>>

    @Transaction
    @Query("SELECT * FROM ${Constants.Tables.ALBUM_TABLE} WHERE name = :albumName")
    suspend fun getAlbumWithSongsByName(albumName: String): AlbumWithSongs?

    @Query("DELETE FROM ${Constants.Tables.ALBUM_TABLE}")
    suspend fun deleteAllAlbums()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllArtists(data: List<Artist>)

    @Transaction
    @Query("SELECT * FROM ${Constants.Tables.ARTIST_TABLE} ORDER BY name ASC")
    fun getAllArtistsWithSongs(): Flow<List<ArtistWithSongs>>

    @Transaction
    @Query("SELECT * FROM ${Constants.Tables.ARTIST_TABLE} WHERE name = :artistName")
    suspend fun getArtistWithSongsByName(artistName: String): ArtistWithSongs?

    @Query("DELETE FROM ${Constants.Tables.ARTIST_TABLE}")
    suspend fun deleteAllArtists()

    @Insert(entity = Playlist::class)
    suspend fun insertPlaylist(playlist: PlaylistExceptId): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistSongCrossRef(playlistSongCrossRefs: List<PlaylistSongCrossRef>)

    @Query("SELECT * FROM ${Constants.Tables.PLAYLIST_TABLE}")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM ${Constants.Tables.PLAYLIST_TABLE} WHERE playlistId = :playlistId")
    suspend fun getPlaylistWithSongs(playlistId: Long): PlaylistWithSongs?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllAlbumArtists(data: List<AlbumArtist>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllComposers(data: List<Composer>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllLyricists(data: List<Lyricist>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllGenres(data: List<Genre>)
}