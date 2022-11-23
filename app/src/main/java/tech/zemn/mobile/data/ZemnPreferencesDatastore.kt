package tech.zemn.mobile.data

import androidx.datastore.core.DataStore
import tech.zemn.mobile.data.UserPreferences.Theme
import javax.inject.Inject

class ZemnPreferencesDatastore @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val preferences = userPreferences.data

    suspend fun setTheme(useMaterialYou: Boolean, theme: Theme) {
        userPreferences.updateData {
            it.copy {
                useMaterialYouTheme = useMaterialYou
                chosenTheme = theme
            }
        }
    }
}