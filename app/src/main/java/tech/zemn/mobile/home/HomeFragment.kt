package tech.zemn.mobile.home

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tech.zemn.mobile.Constants
import tech.zemn.mobile.Screens
import tech.zemn.mobile.SharedViewModel
import tech.zemn.mobile.player.ZemnBroadcastReceiver
import tech.zemn.mobile.ui.theme.ZemnTheme
import tech.zemn.mobile.R

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var navController: NavController

    private val viewModel by activityViewModels<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        return ComposeView(requireContext()).apply {
            setContent {
                ZemnTheme {
                    var currentScreen by remember { mutableStateOf<Screens>(Screens.Home.AllSongs) }
                    val songs by viewModel.songs.collectAsState()
                    val allSongsListState = rememberLazyListState()
                    val currentSong by viewModel.currentSong.collectAsState()
                    val songPlaying by viewModel.currentSongPlaying.collectAsState()
                    val pendingPausePlayIntent = remember {
                        PendingIntent.getBroadcast(
                            context, ZemnBroadcastReceiver.PAUSE_PLAY_ACTION_REQUEST_CODE,
                            Intent(Constants.PACKAGE_NAME).putExtra(
                                ZemnBroadcastReceiver.AUDIO_CONTROL,
                                ZemnBroadcastReceiver.ZEMN_PLAYER_PAUSE_PLAY
                            ),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    }
                    val albumsWithSongs by viewModel.albumsWithSongs.collectAsState()
                    val allAlbumsGridState = rememberLazyGridState()
                    Scaffold(
                        topBar = {
                            HomeTopBar()
                        },
                        content = { paddingValues ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                HomeContent(
                                    currentScreen = currentScreen,
                                    onSongClicked = viewModel::onSongClicked,
                                    songs = songs,
                                    allSongsListState = allSongsListState,
                                    paddingValues = paddingValues,
                                    albumsWithSongs = albumsWithSongs,
                                    allAlbumsGridState = allAlbumsGridState,
                                    onAlbumClicked = viewModel::onAlbumClicked,
                                )
                                if (currentSong != null && songPlaying != null) {
                                    MiniPlayer(
                                        showPlayButton = !songPlaying!!,
                                        onPausePlayPressed = {
                                            pendingPausePlayIntent.send()
                                        },
                                        song = currentSong!!,
                                        albumArt = viewModel.currentSongBitmap.value,
                                        paddingValues = paddingValues,
                                        onMiniPlayerClicked = {
                                            navController.navigate(R.id.action_homeFragment_to_nowPlaying)
                                        }
                                    )
                                }
                            }
                        },
                        bottomBar = {
                            HomeBottomBar(
                                currentScreen = currentScreen,
                                onScreenChange = {
                                    currentScreen = it
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}