package com.github.pakka_papad.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.music.Folder
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFolder(vararg folder: Folder)

    @Query("SELECT * FROM ${Constants.Tables.FOLDER_TABLE}")
    fun getAllFolders(): Flow<List<Folder>>

    @Delete
    suspend fun deleteFolder(folder: Folder)

}