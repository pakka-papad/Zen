package tech.zemn.mobile.nowplaying

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tech.zemn.mobile.Constants
import tech.zemn.mobile.SharedViewModel
import tech.zemn.mobile.player.ZemnBroadcastReceiver
import tech.zemn.mobile.ui.theme.ZemnTheme
import javax.inject.Inject

@AndroidEntryPoint
class NowPlayingFragment : Fragment() {

    private val viewModel by activityViewModels<SharedViewModel>()

    private lateinit var navController: NavController

    @Inject
    lateinit var exoPlayer: ExoPlayer

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
                    val songPlaying by viewModel.currentSongPlaying.collectAsState()
                    val pendingPausePlayIntent = remember{
                        PendingIntent.getBroadcast(
                            context, ZemnBroadcastReceiver.PAUSE_PLAY_ACTION_REQUEST_CODE,
                            Intent(Constants.PACKAGE_NAME).putExtra(
                                ZemnBroadcastReceiver.AUDIO_CONTROL,
                                ZemnBroadcastReceiver.ZEMN_PLAYER_PAUSE_PLAY
                            ),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    }
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
                                onPausePlayPressed = {
                                     pendingPausePlayIntent.send()
                                },
                                onPreviousPressed = {  },
                                onNextPressed = {  },
                                showPlayButton = !songPlaying!!,
                                exoPlayer = exoPlayer,
                            )
                        }
                    )
                }
            }
        }
    }
}