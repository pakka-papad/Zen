package com.github.pakka_papad.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.github.pakka_papad.Screens
import com.github.pakka_papad.data.music.Song
import com.google.accompanist.systemuicontroller.SystemUiController
import kotlinx.coroutines.delay

@Composable
fun HomeBottomBar(
    currentScreen: Screens,
    onScreenChange: (Screens) -> Unit,
    songPlaying: Boolean?,
    onPlayPausePressed: () -> Unit,
    currentSong: Song?,
    onMiniPlayerClicked: () -> Unit,
    mediaPlayer: ExoPlayer,
    systemUiController: SystemUiController
) {
    var showMiniPlayer by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = songPlaying, key2 = currentSong) {
        showMiniPlayer = songPlaying != null && currentSong != null
    }
    systemUiController.setNavigationBarColor(
        MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp + LocalAbsoluteTonalElevation.current)
    )
    BottomAppBar(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp + LocalAbsoluteTonalElevation.current)
            )
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .height(if (showMiniPlayer) 142.dp else 84.dp),
    ) {
        Column {
            if (showMiniPlayer) {
                MiniPlayer(
                    onPausePlayPressed = onPlayPausePressed,
                    song = currentSong,
                    paddingValues = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
                        .asPaddingValues(),
                    onMiniPlayerClicked = onMiniPlayerClicked,
                    showPlayButton = if (songPlaying != null) !songPlaying else true
                )

                var progress by remember { mutableStateOf(0f) }
                var isPlaying by remember { mutableStateOf(mediaPlayer.isPlaying) }

                DisposableEffect(Unit) {
                    progress = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()
                    val listener = object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying_: Boolean) {
                            isPlaying = isPlaying_
                        }

                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            super.onMediaItemTransition(mediaItem, reason)
                            progress = 0f
                        }
                    }
                    mediaPlayer.addListener(listener)
                    onDispose {
                        mediaPlayer.removeListener(listener)
                    }
                }
                if (isPlaying) {
                    LaunchedEffect(Unit) {
                        while (true) {
                            progress = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()
                            delay(40)
                        }
                    }
                }
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    progress = progress
                )
            }
            Row {
                Screens.values().forEach { screen ->
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
    }
}