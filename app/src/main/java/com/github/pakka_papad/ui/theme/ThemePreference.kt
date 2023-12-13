package com.github.pakka_papad.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.github.pakka_papad.data.UserPreferences
import com.github.pakka_papad.ui.accent_colours.DefaultDarkColors
import com.github.pakka_papad.ui.accent_colours.DefaultLightColors
import com.github.pakka_papad.ui.accent_colours.default_seed
import com.github.pakka_papad.ui.accent_colours.ElmDarkColors
import com.github.pakka_papad.ui.accent_colours.ElmLightColors
import com.github.pakka_papad.ui.accent_colours.elm_seed
import com.github.pakka_papad.ui.accent_colours.JacksonsPurpleDarkColors
import com.github.pakka_papad.ui.accent_colours.JacksonsPurpleLightColors
import com.github.pakka_papad.ui.accent_colours.jacksons_purple_seed
import com.github.pakka_papad.ui.accent_colours.MagentaDarkColors
import com.github.pakka_papad.ui.accent_colours.MagentaLightColors
import com.github.pakka_papad.ui.accent_colours.magenta_seed
import com.github.pakka_papad.ui.accent_colours.MalibuDarkColors
import com.github.pakka_papad.ui.accent_colours.MalibuLightColors
import com.github.pakka_papad.ui.accent_colours.malibu_seed
import com.github.pakka_papad.ui.accent_colours.MelroseDarkColors
import com.github.pakka_papad.ui.accent_colours.MelroseLightColors
import com.github.pakka_papad.ui.accent_colours.melrose_seed

data class ThemePreference(
    val useMaterialYou: Boolean = false,
    val theme: UserPreferences.Theme = UserPreferences.Theme.UNRECOGNIZED,
    val accent: UserPreferences.Accent = UserPreferences.Accent.Default,
)

fun UserPreferences.Accent.getColorScheme(isDark: Boolean): ColorScheme {
    return when (this) {
        UserPreferences.Accent.Default, UserPreferences.Accent.UNRECOGNIZED -> {
            if (isDark) DefaultDarkColors
            else DefaultLightColors
        }
        UserPreferences.Accent.Malibu -> {
            if (isDark) MalibuDarkColors
            else MalibuLightColors
        }
        UserPreferences.Accent.Melrose -> {
            if (isDark) MelroseDarkColors
            else MelroseLightColors
        }
        UserPreferences.Accent.Elm -> {
            if (isDark) ElmDarkColors
            else ElmLightColors
        }
        UserPreferences.Accent.Magenta -> {
            if (isDark) MagentaDarkColors
            else MagentaLightColors
        }
        UserPreferences.Accent.JacksonsPurple -> {
            if (isDark) JacksonsPurpleDarkColors
            else JacksonsPurpleLightColors
        }
    }
}

fun UserPreferences.Accent.getSeedColor(): Color {
    return when (this) {
        UserPreferences.Accent.Default, UserPreferences.Accent.UNRECOGNIZED -> default_seed
        UserPreferences.Accent.Malibu -> malibu_seed
        UserPreferences.Accent.Melrose -> melrose_seed
        UserPreferences.Accent.Elm -> elm_seed
        UserPreferences.Accent.Magenta -> magenta_seed
        UserPreferences.Accent.JacksonsPurple -> jacksons_purple_seed
    }
}