package com.github.pakka_papad.restore_folder

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.data.services.BlacklistService
import com.github.pakka_papad.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestoreFolderViewModel @Inject constructor(
    private val context: Application,
    private val blacklistService: BlacklistService,
): ViewModel() {

    private val _restoreFoldersList = mutableStateListOf<Boolean>()
    val restoreFolderList : List<Boolean> = _restoreFoldersList

    val folders = blacklistService.blacklistedFolders
        .onEach {
            while (_restoreFoldersList.size < it.size) _restoreFoldersList.add(false)
            while (_restoreFoldersList.size > it.size) _restoreFoldersList.removeLast()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun updateRestoreList(index: Int, isSelected: Boolean){
        if (restored.value !is Resource.Idle) return
        if (index >= _restoreFoldersList.size) return
        _restoreFoldersList[index] = isSelected
    }

    private val _restored = MutableStateFlow<Resource<Unit>>(Resource.Idle())
    val restored = _restored.asStateFlow()

    fun restoreFolders(){
        viewModelScope.launch {
            _restored.update { Resource.Loading() }
            val allFolders = folders.value
            val toRestore = _restoreFoldersList.indices
                .filter { _restoreFoldersList[it] }
                .map { allFolders[it] }
            try {
                blacklistService.whitelistFolder(toRestore)
                showToast("Done. Rescan to see all the songs")
                _restored.update { Resource.Success(Unit) }
            } catch (_ : Exception){
                showToast("Some error occurred")
                _restored.update { Resource.Error("") }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}