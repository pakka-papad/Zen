package com.github.pakka_papad.data.daos

import androidx.room.*
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.AlbumArtist
import com.github.pakka_papad.data.music.AlbumArtistWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumArtistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllAlbumArtists(data: List<AlbumArtist>)

    @Query("SELECT * FROM ${Constants.Tables.ALBUM_ARTIST_TABLE} WHERE name LIKE '%' || :query || '%'")
    suspend fun searchAlbumArtists(query: String): List<AlbumArtist>

    @Transaction
    @Query("SELECT * FROM ${Constants.Tables.ALBUM_ARTIST_TABLE} WHERE name = :name")
    fun getAlbumArtistWithSongs(name: String): Flow<AlbumArtistWithSongs?>

    @Transaction
    @Query("DELETE FROM ${Constants.Tables.ALBUM_ARTIST_TABLE} WHERE name IN " +
            "(SELECT albumArtist.name as name FROM ${Constants.Tables.ALBUM_ARTIST_TABLE} as albumArtist LEFT JOIN " +
            "${Constants.Tables.SONG_TABLE} as song ON albumArtist.name = song.albumArtist GROUP BY albumArtist.name " +
            "HAVING COUNT(song.location) = 0)")
    suspend fun cleanAlbumArtistTable()

}