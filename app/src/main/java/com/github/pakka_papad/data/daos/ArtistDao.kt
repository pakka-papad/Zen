package com.github.pakka_papad.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.Artist
import com.github.pakka_papad.data.music.ArtistWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllArtists(data: List<Artist>)

    @Transaction
    @Query("SELECT * FROM ${Constants.Tables.ARTIST_TABLE} ORDER BY name ASC")
    fun getAllArtistsWithSongs(): Flow<List<ArtistWithSongs>>

    @Transaction
    @Query("SELECT * FROM ${Constants.Tables.ARTIST_TABLE} WHERE name = :artistName")
    fun getArtistWithSongsByName(artistName: String): Flow<ArtistWithSongs?>

    @Query("DELETE FROM ${Constants.Tables.ARTIST_TABLE}")
    suspend fun deleteAllArtists()

    @Query("SELECT * FROM ${Constants.Tables.ARTIST_TABLE} WHERE name LIKE '%' || :query || '%'")
    suspend fun searchArtists(query: String): List<Artist>

}