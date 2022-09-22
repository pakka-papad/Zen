package tech.zemn.mobile.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.zemn.mobile.Screens
import tech.zemn.mobile.SharedViewModel

@Composable
fun HomeScreen(
    viewModel: SharedViewModel,
    currentScreen: Screens,
    onScreenChange: (Screens) -> Unit,
){
    val songs by viewModel.songs.collectAsState()
    Scaffold(
        content = {
            when(currentScreen){
                is Screens.Home.AllSongs -> {
                    AllSongs(songs = songs)
                }
                is Screens.Home.Albums -> {
                    Button(
                        onClick = {
                            viewModel.foo()
                        }
                    ) {
                        Text(
                            text = "Scan"
                        )
                    }
                }
                is Screens.Home.Artists -> {

                }
                else -> throw RuntimeException("Invalid currentScreen parameter")
            }
        },
        bottomBar = {
            BottomNavigation(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                elevation = 10.dp
            ) {
                BottomNavigationItem(
                    selected = (currentScreen == Screens.Home.AllSongs),
                    onClick = {
                        onScreenChange(Screens.Home.AllSongs)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                )
                BottomNavigationItem(
                    selected = (currentScreen == Screens.Home.Albums),
                    onClick = {
                        onScreenChange(Screens.Home.Albums)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                )
                BottomNavigationItem(
                    selected = (currentScreen == Screens.Home.Artists),
                    onClick = {
                        onScreenChange(Screens.Home.Artists)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                )
            }
        }
    )
}