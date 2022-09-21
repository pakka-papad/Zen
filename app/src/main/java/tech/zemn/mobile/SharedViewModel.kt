package tech.zemn.mobile

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.zemn.mobile.data.DataManager
import tech.zemn.mobile.data.music.Song
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val context: Application
): ViewModel() {

    private var manager: DataManager = DataManager(context)

    var isScanRunning by mutableStateOf(false)
        private set

    var allSongs by mutableStateOf(listOf<Song>())

    fun foo(){
        viewModelScope.launch(Dispatchers.IO) {
            isScanRunning = true
            manager.scanForMusic()
            allSongs = manager.allSongs
            isScanRunning = false
            Timber.d("Found ${allSongs.size} songs")
        }
    }
}