package tech.zemn.mobile.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tech.zemn.mobile.R
import tech.zemn.mobile.Screens
import tech.zemn.mobile.SharedViewModel
import tech.zemn.mobile.ui.theme.ZemnTheme

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
                    Scaffold(
                        topBar = {
                            HomeTopBar()
                        },
                        content = { paddingValues ->
                           HomeContent(
                               currentScreen = currentScreen,
                               onSongClicked = {
                                   viewModel.onSongClicked(it)
                                   navController.navigate(R.id.action_homeFragment_to_nowPlaying)
                               },
                               songs = songs,
                               allSongsListState = allSongsListState,
                               paddingValues = paddingValues,
                           )
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