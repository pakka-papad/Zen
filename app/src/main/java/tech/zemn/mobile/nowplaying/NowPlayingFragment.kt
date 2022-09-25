package tech.zemn.mobile.nowplaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tech.zemn.mobile.SharedViewModel
import tech.zemn.mobile.ui.theme.ZemnTheme

@AndroidEntryPoint
class NowPlayingFragment : Fragment() {

    private val viewModel by activityViewModels<SharedViewModel>()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        if (viewModel.currentSong.value == null){
            navController.popBackStack()
        }
        return ComposeView(requireContext()).apply {
            setContent {
                ZemnTheme {
                    val song by viewModel.currentSong.collectAsState()
                    Scaffold(
                        topBar = {
                            NowPlayingTopBar(
                                onBackArrowPressed = {
                                    navController.popBackStack()
                                },
                                title = song!!.title
                            )
                        },
                        content = { paddingValues ->
                            NowPlayingScreen(
                                paddingValues = paddingValues,
                                song = song!!,
                                onPausePlayPressed = {  },
                                onPreviousPressed = {  },
                                onNextPressed = {  }
                            )
                        }
                    )
                }
            }
        }
    }
}