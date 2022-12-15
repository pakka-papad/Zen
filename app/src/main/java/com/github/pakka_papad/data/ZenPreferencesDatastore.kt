package com.github.pakka_papad.data

import androidx.datastore.core.DataStore
import com.github.pakka_papad.data.UserPreferences.Accent
import com.github.pakka_papad.data.UserPreferences.Theme
import javax.inject.Inject

class ZenPreferencesDatastore @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val preferences = userPreferences.data

    suspend fun setTheme(useMaterialYou: Boolean, theme: Theme, accent: Accent) {
        userPreferences.updateData {
            it.copy {
                useMaterialYouTheme = useMaterialYou
                chosenTheme = theme
                chosenAccent = accent
            }
        }
    }

    suspend fun setOnBoardingComplete() {
        userPreferences.updateData {
            it.copy {
                onBoardingComplete = true
            }
        }
    }
}