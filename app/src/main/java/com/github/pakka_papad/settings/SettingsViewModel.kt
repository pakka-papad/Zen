package com.github.pakka_papad.settings

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.Screens
import com.github.pakka_papad.data.DataManager
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.data.music.ScanStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val context: Application,
    private val manager: DataManager,
    private val prefs: ZenPreferenceProvider
) : ViewModel() {

    val scanStatus = manager.scanStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 300,
                replayExpirationMillis = 0
            ),
            initialValue = ScanStatus.ScanNotRunning
        )

    fun scanForMusic() {
        manager.scanForMusic()
    }

    private val _tabsSelection = MutableStateFlow<List<Pair<Screens,Boolean>>>(listOf())
    val tabsSelection = _tabsSelection.asStateFlow()

    init {
        val selectedScreens = prefs.selectedTabs.value ?: listOf()
        val allScreens = Screens.values()
        val currentSelection = arrayListOf<Pair<Screens,Boolean>>()
        selectedScreens.forEach {
            try {
                currentSelection += Pair(allScreens[it],true)
            } catch (_: Exception){

            }
        }
        allScreens.forEach {
            if (!selectedScreens.contains(it.ordinal)){
                currentSelection += Pair(it,false)
            }
        }
        _tabsSelection.update { currentSelection.toList() }
    }

    fun onTabsSelectChanged(screen: Screens, isSelected: Boolean){
        viewModelScope.launch {
            val newSelection = _tabsSelection.value.map {
                if (it.first.ordinal == screen.ordinal){
                    Pair(it.first, isSelected)
                } else {
                    it
                }
            }
            _tabsSelection.update { newSelection }
        }
    }

    fun onTabsOrderChanged(fromIndex: Int, toIndex: Int){
        viewModelScope.launch {
            val newOrder = _tabsSelection.value.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }.toList()
            _tabsSelection.update { newOrder }
        }
    }

    fun saveTabsOrder() {
        viewModelScope.launch {
            val order = _tabsSelection.value.filter { it.second }.map { it.first.ordinal }
            if (order.isEmpty()){
                Toast.makeText(context, "Minimum one tab selection is required", Toast.LENGTH_SHORT).show()
            } else if (order.size > 5){
                Toast.makeText(context, "Maximum of five tab selections are allowed", Toast.LENGTH_SHORT).show()
            } else if(!order.contains(Screens.Songs.ordinal)) {
                Toast.makeText(context, "Songs tab cannot be removed", Toast.LENGTH_SHORT).show()
            } else {
                prefs.updateSelectedTabs(order)
            }
        }
    }

}