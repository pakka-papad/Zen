package tech.zemn.mobile.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import tech.zemn.mobile.SharedViewModel
import tech.zemn.mobile.ZemnApp
import tech.zemn.mobile.ui.theme.ZemnTheme

class PlaylistFragment: Fragment() {

    private val viewModel by activityViewModels<SharedViewModel>()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        if (viewModel.playlist.value.songs.isEmpty()){
            navController.popBackStack()
        }
        return ComposeView(requireContext()).apply {
            setContent {
                ZemnTheme {
                    val playlistUi by viewModel.playlist.collectAsState()
                    val songsListState = rememberLazyListState()
                    val currentSong by viewModel.currentSong.collectAsState()
                    Scaffold(
                        topBar = {
                            PlaylistTopBar(
                                topBarTitle = playlistUi.topBarTitle,
                                topBarBackgroundImageUri = playlistUi.topBarBackgroundImageUri,
                                onBackArrowPressed = {
                                    navController.popBackStack()
                                },
                                onPlayAllPressed = {
                                    viewModel.setQueue(playlistUi.songs)
                                },
                                onPlaylistAddToQueuePressed = {
                                    viewModel.addToQueue(playlistUi.songs)
                                }
                            )
                        },
                        content = { paddingValues ->
                            PlaylistContent(
                                paddingValues = paddingValues,
                                songs = playlistUi.songs,
                                songsListState = songsListState,
                                onSongClicked = { index, song ->
                                    viewModel.setQueue(playlistUi.songs, index)
                                },
                                onSongFavouriteClicked = viewModel::changeFavouriteValue,
                                currentSong = currentSong,
                                onAddToQueueClicked = viewModel::addToQueue
                            )
                        },
                        bottomBar = {
                            val density = LocalDensity.current
                            val navBarHeight by remember { mutableStateOf(with(density) { ZemnApp.navBarHeight.toDp() }) }
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(navBarHeight)
                                    .background(Color.Black.copy(alpha = 0.3f))
                            )
                        }
                    )
                }
            }
        }
    }

}