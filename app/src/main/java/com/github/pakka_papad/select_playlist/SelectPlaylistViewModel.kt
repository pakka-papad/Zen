package com.github.pakka_papad.select_playlist

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.R
import com.github.pakka_papad.data.services.BlacklistService
import com.github.pakka_papad.data.services.PlaylistService
import com.github.pakka_papad.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SelectPlaylistViewModel @Inject constructor(
    private val context: Application,
    private val blacklistService: BlacklistService,
    private val playlistService: PlaylistService,
) : ViewModel() {


    private val _selectList = mutableStateListOf<Boolean>()
    val selectList: List<Boolean> = _selectList

    val playlistsWithSongCount = playlistService.playlists
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

    private val _insertState = MutableStateFlow<Resource<Unit>>(Resource.Idle())
    val insertState = _insertState.asStateFlow()

    fun addSongsToPlaylists(songLocations: Array<String>) {
        viewModelScope.launch {
            _insertState.update { Resource.Loading() }
            val playlists = playlistsWithSongCount.value
            val blacklistedSongs = blacklistService.blacklistedSongs
                .first()
                .map { it.location }
                .toSet()
            val validSongs = songLocations.filter { blacklistedSongs.contains(it) }
            val anyBlacklistedSong = songLocations.any { blacklistedSongs.contains(it) }
            var error = false
            selectList.forEachIndexed { index, isSelected ->
                if (!isSelected) return@forEachIndexed
                try {
                    val playlist = playlists[index]
                    playlistService.addSongsToPlaylist(validSongs, playlist.playlistId)
                } catch (e: Exception){
                    Timber.e(e)
                    error = true
                }
            }
            if (!error){
                 showToast(context.getString(R.string.done))
                _insertState.update { Resource.Success(Unit) }
            } else {
                showToast(context.getString(R.string.some_error_occurred))
                _insertState.update { Resource.Error("") }
            }
            if (anyBlacklistedSong){
                showToast(context.getString(R.string.blacklisted_songs_have_not_been_added_to_playlist))
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}