package com.github.pakka_papad.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.Screens
import com.github.pakka_papad.R

@Composable
fun HomeBottomBar(
    currentScreen: Screens,
    onScreenChange: (Screens) -> Unit,
) {
    val paddingValues =
        WindowInsets.systemBars.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal).asPaddingValues()
    NavigationBar(
        modifier = Modifier
            .height(64.dp + paddingValues.calculateBottomPadding())
    ) {
        NavigationBarItem(
            selected = (currentScreen == Screens.AllSongs),
            onClick = {
                onScreenChange(Screens.AllSongs)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.List,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            },
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        )
        NavigationBarItem(
            selected = (currentScreen == Screens.Albums),
            onClick = {
                onScreenChange(Screens.Albums)
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_album_24),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            },
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        )
        NavigationBarItem(
            selected = (currentScreen == Screens.Artists),
            onClick = {
                onScreenChange(Screens.Artists)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            },
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        )
    }
}