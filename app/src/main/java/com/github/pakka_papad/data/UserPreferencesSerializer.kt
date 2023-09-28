package com.github.pakka_papad.data

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences
        get() = UserPreferences.getDefaultInstance().copy {
            useMaterialYouTheme = false
            chosenTheme = UserPreferences.Theme.DARK_MODE
            chosenAccent = UserPreferences.Accent.Elm
            onBoardingComplete = false
            crashlyticsDisabled = false
            playbackParams = UserPreferences.PlaybackParams
                .getDefaultInstance().copy {
                    playbackSpeed = 100
                    playbackPitch = 100
                }
            selectedTabs.apply {
                clear()
                addAll(listOf(0,1,2,3,4))
            }
        }

    override suspend fun readFrom(input: InputStream): UserPreferences =
        try {
            UserPreferences.parseFrom(input)
        } catch (exception: Exception) {
            defaultValue
        }


    override suspend fun writeTo(t: UserPreferences, output: OutputStream) = t.writeTo(output)
}