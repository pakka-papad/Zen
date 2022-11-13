package tech.zemn.mobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.lightColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val theme = lightColors(
    primary = Color(0xFF17C379),
    primaryVariant = Color(0xFF469DA4),
    secondary = Color(0xFFA7A4E7),
    secondaryVariant = Color(0xFFA8F3C2),
    surface = Color(0xFFE0F7E3),
)

@Composable
fun ZemnTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colourScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
        else dynamicLightColorScheme(LocalContext.current)
    } else {
        lightColorScheme(
            primary = Color(0xFF17C379),
            onPrimary = Color(0xFF469DA4),
            secondary = Color(0xFFA7A4E7),
            onSecondary = Color(0xFFA8F3C2),
            surface = Color(0xFFE0F7E3)
        )
    }
    val systemUiController = rememberSystemUiController()
    DisposableEffect(key1 = darkTheme, key2 = systemUiController){
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
        onDispose {  }
    }
    MaterialTheme(
        colorScheme = colourScheme,
        typography = ZemnTypography,
        content = content
    )
}