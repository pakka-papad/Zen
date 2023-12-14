package com.github.pakka_papad.data

import android.os.Build
import androidx.datastore.core.Serializer
import com.github.pakka_papad.Screens
import com.github.pakka_papad.components.SortOptions
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences
        get() = UserPreferences.getDefaultInstance().copy {
            useMaterialYouTheme = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
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
            chosenSortOrder.apply {
                clear()
                put(Screens.Songs.ordinal, SortOptions.TitleASC.ordinal)
                put(Screens.Albums.ordinal, SortOptions.TitleASC.ordinal)
                put(Screens.Artists.ordinal, SortOptions.NameASC.ordinal)
                put(Screens.Genres.ordinal, SortOptions.NameASC.ordinal)
                put(Screens.Playlists.ordinal, SortOptions.NameASC.ordinal)
                put(Screens.Folders.ordinal, SortOptions.Default.ordinal)
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