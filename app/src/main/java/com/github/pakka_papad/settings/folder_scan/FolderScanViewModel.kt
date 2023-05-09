package com.github.pakka_papad.settings.folder_scan

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.data.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FolderScanViewModel @Inject constructor(
    private val context: Application,
    private val manager: DataManager,
): ViewModel() {

    val folders = manager.getAll.folders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}