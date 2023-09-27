package com.github.pakka_papad.settings.blacklisted_folder

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.data.DataManager
import com.github.pakka_papad.data.music.BlacklistedFolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlacklistedFolderViewModel @Inject constructor(
    private val context: Application,
    private val manager: DataManager,
): ViewModel() {

    val folders = manager.getAll.blacklistedFolders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onBlacklistRemoveRequest(folder: BlacklistedFolder){
        viewModelScope.launch {
            try {
                manager.removeFolderFromBlacklist(folder)
                showToast("Done. Rescan to see all the songs")
            } catch (_ : Exception){
                showToast("Some error occurred")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}