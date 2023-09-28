package com.github.pakka_papad.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.Screens

@Composable
fun HomeBottomBar(
    currentScreen: Screens,
    onScreenChange: (Screens) -> Unit,
    bottomBarColor: Color,
    selectedTabs: List<Int>?
) {
    if (selectedTabs == null) return
    val screens = Screens.values()
    BottomAppBar(
        modifier = Modifier
            .background(bottomBarColor)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .height(88.dp),
    ) {
        selectedTabs.filter { it >= 0 && it < screens.size }
            .map { screens[it] }.forEach { screen ->
            NavigationBarItem(
                selected = (currentScreen == screen),
                onClick = {
                    onScreenChange(screen)
                },
                icon = {
                    Icon(
                        painter = painterResource(screen.filledIcon),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    Text(
                        text = screen.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (currentScreen == screen) FontWeight.ExtraBold else FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}