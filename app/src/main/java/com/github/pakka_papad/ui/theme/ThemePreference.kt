package com.github.pakka_papad.ui.theme

import com.github.pakka_papad.data.UserPreferences

data class ThemePreference(
    val useMaterialYou: Boolean = false,
    val theme: UserPreferences.Theme = UserPreferences.Theme.UNRECOGNIZED
)
