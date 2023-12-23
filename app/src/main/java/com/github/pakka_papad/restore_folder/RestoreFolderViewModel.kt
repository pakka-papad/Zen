package com.github.pakka_papad.restore_folder

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.R
import com.github.pakka_papad.data.services.BlacklistService
import com.github.pakka_papad.util.MessageStore
import com.github.pakka_papad.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject

@HiltViewModel
class RestoreFolderViewModel @Inject constructor(
    private val messageStore: MessageStore,
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

    @VisibleForTesting
    internal val _restored = MutableStateFlow<Resource<Unit>>(Resource.Idle())
    val restored = _restored.asStateFlow()

    fun restoreFolders(){
        if (restored.value !is Resource.Idle) return
        viewModelScope.launch {
            _restored.update { Resource.Loading() }
            val allFolders = folders.value
            val toRestore = _restoreFoldersList.indices
                .filter { _restoreFoldersList[it] }
                .map { allFolders[it] }
            try {
                blacklistService.whitelistFolders(toRestore)
                _restored.update { Resource.Success(Unit) }
            } catch (_ : Exception){
                _restored.update { Resource.Error(messageStore.getString(R.string.some_error_occurred)) }
            }
        }
    }
}