package com.github.pakka_papad.ui.accent_colours.elm

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color


val ElmLightColors by lazy {
    lightColorScheme(
        primary = Color(0xFF006972),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF8BF2FF),
        onPrimaryContainer = Color(0xFF001F23),
        secondary = Color(0xFF4A6366),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFCDE7EB),
        onSecondaryContainer = Color(0xFF051F22),
        tertiary = Color(0xFF515E7D),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFD8E2FF),
        onTertiaryContainer = Color(0xFF0C1B36),
        error = Color(0xFFBA1A1A),
        errorContainer = Color(0xFFFFDAD6),
        onError = Color(0xFFFFFFFF),
        onErrorContainer = Color(0xFF410002),
        background = Color(0xFFFAFDFD),
        onBackground = Color(0xFF191C1D),
        surface = Color(0xFFFAFDFD),
        onSurface = Color(0xFF191C1D),
        surfaceVariant = Color(0xFFDAE4E6),
        onSurfaceVariant = Color(0xFF3F484A),
        outline = Color(0xFF6F797A),
        inverseOnSurface = Color(0xFFEFF1F1),
        inverseSurface = Color(0xFF2D3131),
        inversePrimary = Color(0xFF4ED8E8),
        surfaceTint = Color(0xFF006972),
    )   
}


val ElmDarkColors by lazy {
    darkColorScheme(
        primary = Color(0xFF4ED8E8),
        onPrimary = Color(0xFF00363C),
        primaryContainer = Color(0xFF004F56),
        onPrimaryContainer = Color(0xFF8BF2FF),
        secondary = Color(0xFFB1CBCF),
        onSecondary = Color(0xFF1C3437),
        secondaryContainer = Color(0xFF324B4E),
        onSecondaryContainer = Color(0xFFCDE7EB),
        tertiary = Color(0xFFB9C6EA),
        onTertiary = Color(0xFF22304D),
        tertiaryContainer = Color(0xFF394664),
        onTertiaryContainer = Color(0xFFD8E2FF),
        error = Color(0xFFFFB4AB),
        errorContainer = Color(0xFF93000A),
        onError = Color(0xFF690005),
        onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF191C1D),
        onBackground = Color(0xFFE0E3E3),
        surface = Color(0xFF191C1D),
        onSurface = Color(0xFFE0E3E3),
        surfaceVariant = Color(0xFF3F484A),
        onSurfaceVariant = Color(0xFFBEC8CA),
        outline = Color(0xFF899294),
        inverseOnSurface = Color(0xFF191C1D),
        inverseSurface = Color(0xFFE0E3E3),
        inversePrimary = Color(0xFF006972),
        surfaceTint = Color(0xFF4ED8E8),
    )   
}

val elm_seed by lazy { Color(0xFF247881) }