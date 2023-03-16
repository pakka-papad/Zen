package com.github.pakka_papad.data

import androidx.datastore.core.DataStore
import com.github.pakka_papad.data.UserPreferences.PlaybackParams
import com.github.pakka_papad.ui.theme.ThemePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

class ZenPreferenceProvider @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
    private val coroutineScope: CoroutineScope,
    private val crashReporter: ZenCrashReporter,
) {

    val theme = userPreferences.data
        .map {
            ThemePreference(
                useMaterialYou = it.useMaterialYouTheme,
                theme = it.chosenTheme,
                accent = it.chosenAccent,
            )
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemePreference()
        )

    fun updateTheme(newThemePreference: ThemePreference) {
        coroutineScope.launch {
            userPreferences.updateData {
                it.copy {
                    useMaterialYouTheme = newThemePreference.useMaterialYou
                    chosenTheme = newThemePreference.theme
                    chosenAccent = newThemePreference.accent
                }
            }
        }
    }

    val isOnBoardingComplete = userPreferences.data
        .map {
            it.onBoardingComplete
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun setOnBoardingComplete() {
        coroutineScope.launch {
            userPreferences.updateData {
                it.copy {
                    onBoardingComplete = true
                }
            }
        }
    }

    val isCrashlyticsDisabled = userPreferences.data
        .map {
            it.crashlyticsDisabled
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun toggleCrashlytics(autoReportCrash: Boolean) {
        coroutineScope.launch {
            crashReporter.sendCrashData(autoReportCrash)
            userPreferences.updateData {
                it.copy {
                    crashlyticsDisabled = !autoReportCrash
                }
            }
        }
    }

    val playbackParams = userPreferences.data
        .map {
            it.playbackParams
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaybackParams
                .getDefaultInstance().copy {
                    playbackSpeed = 100
                    playbackPitch = 100
                }
        )

    fun updatePlaybackParams(speed: Int, pitch: Int){
        coroutineScope.launch {
            val correctedParams = PlaybackParams.getDefaultInstance().copy{
                playbackSpeed = if (speed < 1 || speed > 200) 100 else speed
                playbackPitch = if (pitch < 1 || pitch > 200) 100 else pitch
            }
            userPreferences.updateData {
                it.copy {
                    playbackParams = correctedParams
                }
            }
        }
    }

    init {
        val initJob = coroutineScope.launch {
            launch { theme.collect { } }
            launch { isOnBoardingComplete.collect { } }
            launch { isCrashlyticsDisabled.collect { } }
            launch { playbackParams.collect { updatePlaybackParams(it.playbackSpeed,it.playbackPitch) } }
        }
        coroutineScope.launch {
            delay(1.minutes)
            initJob.cancel()
        }
    }
}