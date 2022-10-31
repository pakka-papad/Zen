package tech.zemn.mobile.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import tech.zemn.mobile.MainActivity
import tech.zemn.mobile.R
import tech.zemn.mobile.SharedViewModel
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
        requireActivity().window.apply {
            navigationBarColor = ContextCompat.getColor(requireContext(),R.color.scrim_color)
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
                                paddingValues = PaddingValues(bottom = MainActivity.bottom.dp),
                                songs = playlistUi.songs,
                                songsListState = songsListState,
                                onSongClicked = { index, song ->
                                    viewModel.setQueue(playlistUi.songs, index)
                                },
                                onSongFavouriteClicked = viewModel::changeFavouriteValue,
                                currentSong = currentSong,
                                onAddToQueueClicked = viewModel::addToQueue
                            )
                        }
                    )
                }
            }
        }
    }

}