package com.github.pakka_papad.data

import androidx.datastore.core.DataStore
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

    val playbackSpeed = userPreferences.data
        .map {
            it.playbackSpeed
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 100
        )

    fun updatePlaybackSpeed(newPlaybackSpeed: Int) {
        coroutineScope.launch {
            userPreferences.updateData {
                it.copy {
                    this.playbackSpeed = if (newPlaybackSpeed < 10 || newPlaybackSpeed > 200) 100
                    else newPlaybackSpeed
                }
            }
        }
    }

    init {
        val initJob = coroutineScope.launch {
            launch { theme.collect { } }
            launch { isOnBoardingComplete.collect { } }
            launch {
                isCrashlyticsDisabled.collect { crashReporter.sendCrashData(!it) }
            }
            launch {
                playbackSpeed.collect {
                    if (it < 10 || it > 200) updatePlaybackSpeed(100)
                }
            }
        }
        coroutineScope.launch {
            delay(1.minutes)
            initJob.cancel()
        }
    }
}