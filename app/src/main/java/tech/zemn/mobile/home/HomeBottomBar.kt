package tech.zemn.mobile.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import tech.zemn.mobile.R
import tech.zemn.mobile.Screens

@Composable
fun HomeBottomBar(
    currentScreen: Screens,
    onScreenChange: (Screens) -> Unit,
){
    BottomNavigation(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        elevation = 10.dp
    ) {
        BottomNavigationItem(
            selected = (currentScreen == Screens.AllSongs),
            onClick = {
                onScreenChange(Screens.AllSongs)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.List,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
        )
        BottomNavigationItem(
            selected = (currentScreen == Screens.Albums),
            onClick = {
                onScreenChange(Screens.Albums)
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_album_24),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
        )
        BottomNavigationItem(
            selected = (currentScreen == Screens.Artists),
            onClick = {
                onScreenChange(Screens.Artists)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
        )
    }
}