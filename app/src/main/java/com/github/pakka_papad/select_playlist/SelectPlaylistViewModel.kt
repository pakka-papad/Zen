package com.github.pakka_papad.select_playlist

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.data.DataManager
import com.github.pakka_papad.data.music.PlaylistSongCrossRef
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SelectPlaylistViewModel @Inject constructor(
    private val context: Application,
    private val manager: DataManager,
) : ViewModel() {


    private val _selectList = mutableStateListOf<Boolean>()
    val selectList: List<Boolean> = _selectList

    val playlistsWithSongCount = manager.getAll.playlists()
        .onEach {
            while (_selectList.size < it.size) _selectList.add(false)
            while (_selectList.size > it.size) _selectList.removeLast()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun toggleSelectAtIndex(index: Int) {
        if (index >= _selectList.size) return
        _selectList[index] = !_selectList[index]
    }

    fun addSongsToPlaylists(songLocations: Array<String>) {
        viewModelScope.launch {
            val playlists = playlistsWithSongCount.value
            val playlistSongCrossRefs = _selectList.indices
                .filter { _selectList[it] }
                .map {
                    val list = ArrayList<PlaylistSongCrossRef>()
                    for (songLocation in songLocations) {
                        list += PlaylistSongCrossRef(playlists[it].playlistId, songLocation)
                    }
                    list.toList()
                }
            try {
                manager.insertPlaylistSongCrossRefs(playlistSongCrossRefs.flatten())
                Toast.makeText(context,"Done",Toast.LENGTH_SHORT).show()
            } catch (e: Exception){
                Timber.e(e)
                Toast.makeText(context,"Some error occurred",Toast.LENGTH_SHORT).show()
            }
        }
    }
}