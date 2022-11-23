package tech.zemn.mobile.ui.theme

import tech.zemn.mobile.data.UserPreferences

data class ThemePreference(
    val useMaterialYou: Boolean = false,
    val theme: UserPreferences.Theme = UserPreferences.Theme.UNRECOGNIZED
)
