package com.github.pakka_papad.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.Constants
import com.github.pakka_papad.R
import com.github.pakka_papad.Screens
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.data.music.ScanStatus
import com.github.pakka_papad.data.music.SongExtractor
import com.github.pakka_papad.util.MessageStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val songExtractor: SongExtractor,
    private val prefs: ZenPreferenceProvider,
    private val messageStore: MessageStore,
) : ViewModel() {

    val scanStatus = songExtractor.scanStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 300,
                replayExpirationMillis = 0
            ),
            initialValue = ScanStatus.ScanNotRunning
        )

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    private fun showMessage(message: String){
        viewModelScope.launch {
            _message.update { message }
            delay(Constants.MESSAGE_DURATION)
            _message.update { "" }
        }
    }

    fun scanForMusic() {
        songExtractor.scanForMusic()
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
                showMessage(messageStore.getString(R.string.minimum_one_tab_selection_is_required))
            } else if (order.size > 5){
                showMessage(messageStore.getString(R.string.maximum_of_five_tab_selections_are_allowed))
            } else if(!order.contains(Screens.Songs.ordinal)) {
                showMessage(messageStore.getString(R.string.songs_tab_cannot_be_removed))
            } else {
                prefs.updateSelectedTabs(order)
                showMessage(messageStore.getString(R.string.done))
            }
        }
    }

}