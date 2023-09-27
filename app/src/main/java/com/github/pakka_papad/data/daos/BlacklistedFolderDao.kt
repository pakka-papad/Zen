package com.github.pakka_papad.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.BlacklistedFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface BlacklistedFolderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFolder(vararg folder: BlacklistedFolder)

    @Query("SELECT * FROM ${Constants.Tables.BLACKLISTED_FOLDER_TABLE}")
    fun getAllFolders(): Flow<List<BlacklistedFolder>>

    @Delete
    suspend fun deleteFolder(folder: BlacklistedFolder)

}