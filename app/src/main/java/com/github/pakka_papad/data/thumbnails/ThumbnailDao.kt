package com.github.pakka_papad.data.thumbnails

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.pakka_papad.Constants

@Dao
interface ThumbnailDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Thumbnail::class)
    suspend fun insert(thumbnail: ThumbnailWithoutId)

    @Query("SELECT * FROM ${Constants.Tables.THUMBNAIL_TABLE} WHERE location = :location")
    suspend fun getThumbnail(location: String): Thumbnail?

    @Delete(entity = Thumbnail::class)
    suspend fun delete(thumbnail: Thumbnail)

    @Query("UPDATE ${Constants.Tables.THUMBNAIL_TABLE} SET deleteThis = 1 WHERE location = :location")
    suspend fun markDelete(location: String)
}