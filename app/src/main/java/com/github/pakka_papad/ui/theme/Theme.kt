package com.github.pakka_papad.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.github.pakka_papad.data.UserPreferences
import com.github.pakka_papad.ui.accent_colours.default.DefaultDarkColors
import com.github.pakka_papad.ui.accent_colours.default.DefaultLightColors
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController



@Composable
fun DefaultTheme(content: @Composable () -> Unit) {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val systemUiController = rememberSystemUiController()
    DisposableEffect(key1 = isSystemInDarkTheme) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !isSystemInDarkTheme
        )
        onDispose { }
    }
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme) DefaultDarkColors else DefaultLightColors,
        typography = ZenTypography,
        content = content
    )
}

@Composable
fun ZenTheme(
    themePreference: ThemePreference,
    systemUiController: SystemUiController = rememberSystemUiController(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val isDark = when(themePreference.theme){
        UserPreferences.Theme.LIGHT_MODE, UserPreferences.Theme.UNRECOGNIZED -> false
        UserPreferences.Theme.DARK_MODE -> true
        UserPreferences.Theme.USE_SYSTEM_MODE -> isSystemInDarkTheme()
    }
    val colourScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && themePreference.useMaterialYou) {
        if (isDark) dynamicDarkColorScheme(context)
        else dynamicLightColorScheme(context)
    } else {
        themePreference.accent.getColorScheme(isDark)
    }
    DisposableEffect(key1 = themePreference.theme, key2 = systemUiController) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !isDark
        )
        onDispose { }
    }
    MaterialTheme(
        colorScheme = colourScheme,
        typography = ZenTypography,
        content = content
    )
}