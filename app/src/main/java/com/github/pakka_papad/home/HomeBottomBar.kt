package com.github.pakka_papad.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.Screens

@Composable
fun HomeBottomBar(
    currentScreen: Screens,
    onScreenChange: (Screens) -> Unit,
) {
    NavigationBar(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)),

        ) {
        Screens.values().forEach { screen ->
            NavigationBarItem(
                selected = (currentScreen == screen),
                onClick = {
                    onScreenChange(screen)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    Text(
                        text = screen.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (currentScreen == screen) FontWeight.ExtraBold else FontWeight.Bold
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}